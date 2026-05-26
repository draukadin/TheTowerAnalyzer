package com.pphi.tower.parser;

import com.pphi.tower.exceptions.FieldToLineCountMismatchException;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerEra;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlehistory.*;
import com.pphi.tower.model.googledrive.BattleReportDriveFile;
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

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    public BattleHistory parse(final Path path) {
        try {
            final List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return parse(allLines);
        } catch (FieldToLineCountMismatchException ex) {
            throw new RuntimeException(String.format("%s - %s", ex.getMessage(), path));
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
            if (fields.length != lines.size()) {
                throw new FieldToLineCountMismatchException(String.format("%s has %d fields and %d lines", sectionHeader, fields.length, lines.size()));
            }
            final Object[] initArgs = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String line = lines.get(i);
                initArgs[i] = parseLine(field, line);
            }
            try {
                sectionMap.put(sectionHeader, (Section) sectionHeader.getType().getConstructors()[0].newInstance(initArgs));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }));
        return new BattleHistory(sectionMap);
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

    private Object parseString(String line) {
        try {
            return line.split("\t")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new RuntimeException(String.format("Failed to parse %s", line), ex);
        }
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
        final String value = line.split("\t")[1];
        return Long.parseLong(value);
    }

    private Object parseInt(String line) {
        final String value = line.split("\t")[1];
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
            final String value = line.split("\t")[1];
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
