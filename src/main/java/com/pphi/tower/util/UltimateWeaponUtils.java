package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public final class UltimateWeaponUtils {

    private UltimateWeaponUtils() {}

    public static String getName(List<ValueRange> valueRanges) {
        return ValueRangeUtils.getValue(valueRanges, UwSheetLayout.NAME_ROW, UwSheetLayout.NAME_COL);
    }

    public static boolean isLocked(List<ValueRange> valueRanges) {
        return !Boolean.parseBoolean(
                ValueRangeUtils.getValue(valueRanges, UwSheetLayout.NAME_ROW, UwSheetLayout.LOCKED_COL));
    }

    public static Number getStatOne(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_ONE_ROW, UwSheetLayout.STAT_COL)));
    }

    public static Number getStatTwo(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_TWO_ROW, UwSheetLayout.STAT_COL)));
    }

    public static Number getStatThree(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_THREE_ROW, UwSheetLayout.STAT_COL)));
    }

    public static boolean isUwPlusLocked(List<ValueRange> valueRanges) {
        return !Boolean.parseBoolean(
                ValueRangeUtils.getValue(valueRanges, UwSheetLayout.UW_PLUS_ROW, UwSheetLayout.UW_PLUS_LOCKED_COL));
    }

    public static Number uwPlusStat(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.UW_PLUS_ROW, UwSheetLayout.UW_PLUS_STAT_COL)));
    }

    public static int stonedInvestedOne(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_ONE_ROW, UwSheetLayout.STONES_INVESTED_COL));
    }

    public static int stonedRequiredOne(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_ONE_ROW, UwSheetLayout.STONES_REQUIRED_COL));
    }

    public static int stonedInvestedTwo(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_TWO_ROW, UwSheetLayout.STONES_INVESTED_COL));
    }

    public static int stonedRequiredTwo(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_TWO_ROW, UwSheetLayout.STONES_REQUIRED_COL));
    }

    public static int stonedInvestedThree(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_THREE_ROW, UwSheetLayout.STONES_INVESTED_COL));
    }

    public static int stonedRequiredThree(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.STAT_THREE_ROW, UwSheetLayout.STONES_REQUIRED_COL));
    }

    public static int stonedInvestedUwPlus(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.UW_PLUS_ROW, UwSheetLayout.STONES_INVESTED_COL));
    }

    public static int stonedRequiredUwPlus(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, UwSheetLayout.UW_PLUS_ROW, UwSheetLayout.STONES_REQUIRED_COL));
    }

    private static String sanitize(String s) {
        return s.replace("x", "")
                .replace("#", "")
                .replace("s", "")
                .replace("m", "")
                .replace("%", "")
                .replace("Locked", "0");
    }
}
