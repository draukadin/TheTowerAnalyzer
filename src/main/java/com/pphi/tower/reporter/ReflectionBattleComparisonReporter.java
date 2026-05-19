package com.pphi.tower.reporter;

import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.Section;
import com.pphi.tower.model.battlehistory.SectionHeader;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Set;

public class ReflectionBattleComparisonReporter implements Reporter {

    // ANSI color codes
    private static final String GREEN = "\u001B[32m";
    private static final String RED   = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private enum ColorStrategy {
        HIGHER_IS_BETTER,
        LOWER_IS_BETTER,
        NEUTRAL
    }

    /**
     * Fields that carry no meaningful numeric delta (Strings, Instants, etc.)
     */
    private static final Set<String> NEUTRAL_TYPES = Set.of(
            "java.time.Instant",
            "java.lang.String"
    );

    /**
     * Scoped as "SECTION_HEADER_NAME.fieldName" to avoid false matches when the
     * same field name appears in multiple sections (e.g. "tower" in DamageTaken
     * vs a hypothetical "tower" elsewhere).
     */
    private static final Set<String> SCOPED_LOWER_IS_BETTER = Set.of(
            "DAMAGE_TAKEN.tower",
            "DAMAGE_TAKEN.wall"
    );

    public void printReport(List<BattleHistory> battles) {
        BattleHistory report1 = battles.get(0);
        BattleHistory report2 = battles.get(1);
        BattleHistory delta   = battles.get(2);

        // Pre-compute column widths from actual data
        int colLabel = 0;
        int colVal   = 0;

        for (SectionHeader header : SectionHeader.values()) {
            Section s1 = report1.sectionMap().get(header);
            Section s2 = report2.sectionMap().get(header);
            Section sd = delta.sectionMap().get(header);
            if (s1 == null) continue;

            for (RecordComponent component : header.getType().getRecordComponents()) {
                colLabel = Math.max(colLabel, camelToLabel(component.getName()).length());
                colVal   = Math.max(colVal,   extract(s1, component).length());
                colVal   = Math.max(colVal,   extract(s2, component).length());
                colVal   = Math.max(colVal,   extract(sd, component).length());
            }
        }

        // Add padding
        colLabel += 2;
        colVal   += 2;

        printHeader(colLabel, colVal);

        for (SectionHeader header : SectionHeader.values()) {
            Section s1 = report1.sectionMap().get(header);
            Section s2 = report2.sectionMap().get(header);
            Section sd = delta.sectionMap().get(header);
            if (s1 == null) continue;

            printSectionHeader(header.getName(), colLabel, colVal);

            for (RecordComponent component : header.getType().getRecordComponents()) {
                String fieldName = component.getName();
                String label     = camelToLabel(fieldName);
                String v1        = extract(s1, component);
                String v2        = extract(s2, component);
                String dv        = extract(sd, component);

                printRow(label, v1, v2, dv, resolveStrategy(header, component), colLabel, colVal);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Reflection
    // -------------------------------------------------------------------------

    private String extract(Section section, RecordComponent component) {
        if (section == null) return "-";
        try {
            Object value = component.getAccessor().invoke(section);
            return value == null ? "-" : value.toString();
        } catch (Exception e) {
            return "ERR";
        }
    }

    // -------------------------------------------------------------------------
    // Color strategy
    // -------------------------------------------------------------------------

    private ColorStrategy resolveStrategy(SectionHeader header, RecordComponent component) {
        if (NEUTRAL_TYPES.contains(component.getType().getName())) {
            return ColorStrategy.NEUTRAL;
        }
        if (SCOPED_LOWER_IS_BETTER.contains(header.name() + "." + component.getName())) {
            return ColorStrategy.LOWER_IS_BETTER;
        }
        return ColorStrategy.HIGHER_IS_BETTER;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    private void printHeader(int colLabel, int colVal) {
        System.out.println();
        System.out.printf("%-" + colLabel + "s %-" + colVal + "s %-" + colVal + "s %-" + colVal + "s%n",
                "", "Report 1", "Report 2", "Delta");
        System.out.println("=".repeat(colLabel + colVal * 3 + 3));
    }

    private void printSectionHeader(String title, int colLabel, int colVal) {
        System.out.println();
        System.out.println(title);
        System.out.println("-".repeat(colLabel + colVal * 3 + 3));
    }

    private void printRow(String label, String v1, String v2, String delta, ColorStrategy strategy, int colLabel, int colVal) {
        System.out.printf("%-" + colLabel + "s %-" + colVal + "s %-" + colVal + "s %s%n",
                label, v1, v2, colorize(delta, strategy));
    }

    private String colorize(String delta, ColorStrategy strategy) {
        if (strategy == ColorStrategy.NEUTRAL) return delta;

        boolean isNegative = delta.startsWith("-");
        boolean isZero     = delta.equals("0") || delta.startsWith("0.00");

        if (isZero) return delta;

        boolean isGood = switch (strategy) {
            case HIGHER_IS_BETTER -> !isNegative;
            case LOWER_IS_BETTER  ->  isNegative;
            default               -> false;
        };

        return (isGood ? GREEN : RED) + delta + RESET;
    }

    // -------------------------------------------------------------------------
    // camelCase -> "Title Case Words"
    // e.g. "coinsPerHour" -> "Coins Per Hour"
    // -------------------------------------------------------------------------

    private String camelToLabel(String camel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (i == 0) {
                sb.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                sb.append(' ').append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
