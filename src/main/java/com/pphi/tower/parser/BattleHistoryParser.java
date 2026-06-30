package com.pphi.tower.parser;

import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerEra;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlehistory.*;
import com.pphi.tower.model.googledrive.BattleReportDriveFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BattleHistoryParser {

    private static final Logger log = LoggerFactory.getLogger(BattleHistoryParser.class);

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public BattleHistory parse(final Path path) {
        try {
            final List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return parse(allLines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file.", e);
        }
    }

    public BattleHistory parse(final BattleReportDriveFile file) {
        return parse(file.contents().lines().toList());
    }

    public BattleHistory parse(final List<String> allLines) {
        Map<SectionHeader, List<String>> sectionHeaderListMap = readAllLines(allLines);
        Map<SectionHeader, Section> sectionMap = new HashMap<>();
        sectionHeaderListMap.forEach(((sectionHeader, lines) -> {
            final Field[] fields = sectionHeader.getType().getDeclaredFields();
            // The game appends new stat lines to the end of a section as it evolves (e.g. v28.3
            // added rows to Health Regenerated and Killed With Effect Active). Map positionally
            // over the overlap so we tolerate reports from any version: older reports leave the
            // newer trailing fields at their type default; newer reports with extra trailing
            // lines we don't model yet are ignored. A mismatch is logged for visibility.
            if (lines.size() > fields.length) {
                // The report carries more stat rows than we model — a newer game version likely
                // appended fields we should add. Worth surfacing.
                log.warn("Section {} has {} fields but report has {} lines — extra trailing lines ignored; consider modelling the new stat(s)",
                        sectionHeader, fields.length, lines.size());
            } else if (lines.size() < fields.length) {
                // Fewer rows than we model — an older report predating later stat additions.
                // Expected and benign; the newer trailing fields default. Keep it quiet.
                log.debug("Section {} has {} fields but report has {} lines — older report, trailing fields defaulted",
                        sectionHeader, fields.length, lines.size());
            }
            final int common = Math.min(fields.length, lines.size());
            final Object[] initArgs = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                initArgs[i] = (i < common) ? parseLine(fields[i], lines.get(i)) : defaultFor(fields[i].getType());
            }
            try {
                sectionMap.put(sectionHeader, (Section) sectionHeader.getType().getConstructors()[0].newInstance(initArgs));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }));
        return new BattleHistory(sectionMap);
    }

    /** Default value for a section field absent from the report (older game version). */
    private Object defaultFor(Class<?> type) {
        if (type == long.class)             return 0L;
        if (type == int.class)              return 0;
        if (type == double.class)           return 0.0;
        if (type == boolean.class)          return false;
        if (type == TowerNumber.class)      return TowerNumber.ZERO;
        return null; // String, Duration, Instant, TowerEra, etc.
    }

    private Object parseLine(Field field, String line) {
        String name = field.getType().getName();
        switch (name) {
            case "com.pphi.tower.model.TowerNumber" -> {
                return parseTowerNumber(line);
            }
            case "long" -> {
                return parseLong(line);
            }
            case "int" -> {
                return parseInt(line);
            }
            case "java.time.Instant" -> {
                return parseInstant(line);
            }
            case "java.time.Duration" -> {
                return parseDuration(line);
            }
            case "java.lang.String" -> {
                return parseString(line);
            }
            case "com.pphi.tower.model.TowerEra" -> {
                return TowerEra.parse(line);
            }
            default -> throw new RuntimeException(String.format("No handling for %s", name));
        }
    }

    // As of game v28.3, numeric stat cells may carry a trailing percentage annotation
    // in square brackets (e.g. "126 [3.5%]" on "Killed With Effect Active" rows). Strip
    // it so the leading number parses cleanly; values without an annotation are unaffected.
    private static final java.util.regex.Pattern PERCENT_ANNOTATION =
            java.util.regex.Pattern.compile("\\s*\\[[^\\]]*\\]");

    private static String stripAnnotation(String value) {
        return PERCENT_ANNOTATION.matcher(value).replaceAll("").trim();
    }

    private Object parseString(String line) {
        String[] parts = line.split("\t");
        if (parts.length < 2) {
            return "Surrendered";
        }
        return parts[1];
    }

    private Object parseDuration(String line) {
        final String value = line.split("\t")[1];
        String[] parts = value.split(" ");
        long days = 0, hours = 0, minutes = 0, seconds = 0;

        for (String part : parts) {
            if (part.endsWith("d")) days = Long.parseLong(part.replace("d", ""));
            else if (part.endsWith("h")) hours = Long.parseLong(part.replace("h", ""));
            else if (part.endsWith("m")) minutes = Long.parseLong(part.replace("m", ""));
            else if (part.endsWith("s")) seconds = Long.parseLong(part.replace("s", ""));
        }
        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    private Object parseInstant(String line) {
        final String value = line.split("\t")[1];
        LocalDateTime localDateTime = LocalDateTime.parse(value, DTF);
        return localDateTime.atZone(ZoneId.of("America/Los_Angeles")).toInstant();
    }

    private Object parseLong(String line) {
        final String value = stripAnnotation(line.split("\t")[1]);
        return Long.parseLong(value);
    }

    private Object parseInt(String line) {
        final String value = stripAnnotation(line.split("\t")[1]);
        return Integer.parseInt(value);
    }

    private Map<SectionHeader, List<String>> readAllLines(final List<String> lines) {
        final Map<SectionHeader, List<String>> sectionHeaderListHashMap = new HashMap<>();
        final AtomicInteger startIndex = new AtomicInteger();
        SectionHeader sectionHeader = SectionHeader.fromName(lines.get(0));
        int endIndex = 0;
        for (int i = 1; i < lines.size(); i++) {
            startIndex.set(endIndex + 1);
            endIndex = findEndIndex(endIndex + 1, lines);
            if (startIndex.get() < lines.size()) {
                List<String> sectionLines = lines.subList(startIndex.get(), endIndex);
                sectionHeaderListHashMap.put(sectionHeader, sectionLines);
                if (endIndex < lines.size()) {
                    sectionHeader = SectionHeader.fromName(lines.get(endIndex));
                }
            }
        }
        return sectionHeaderListHashMap;
    }

    private int findEndIndex(int startIndex, List<String> lines) {
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (SectionHeader.fromName(line) != null) {
                return i;
            }
        }
        return lines.size();
    }

    public TowerNumber parseTowerNumber(final String line) {
        try {
            final String value = stripAnnotation(line.split("\t")[1]);
            int endIndex = value.length() - 1;
            String amountValue = value.substring(0, endIndex).replace("$", "");
            double amount;
            if (amountValue.isBlank()) {
                amount = 0.0;
            }  else {
                amount = Double.parseDouble(amountValue);
            }
            String suffix = value.substring(endIndex);
            ScaleSuffix scaleSuffix = ScaleSuffix.fromSuffix(suffix);
            return new TowerNumber(BigDecimal.valueOf(amount), scaleSuffix);
        } catch (RuntimeException ex) {
            throw new RuntimeException(String.format("Failed to parse %s", line), ex);
        }
    }
}
