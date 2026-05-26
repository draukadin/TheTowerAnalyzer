package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public final class UltimateWeaponUtils {

    public static String getName(List<ValueRange> valueRanges) {
        return ValueRangeUtils.getValue(valueRanges, 0, 0);
    }

    public static boolean isLocked(List<ValueRange> valueRanges) {
        return !Boolean.parseBoolean(ValueRangeUtils.getValue(valueRanges, 0, 3));
    }

    public static Number getStatOne(List<ValueRange> valueRanges) {
        return Integer.parseInt(sanitize(ValueRangeUtils.getValue(valueRanges, 2, 2)));
    }

    public static Number getStatOne(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, 2, 2)));
    }

    public static Number getStatTwo(List<ValueRange> valueRanges) {
        return Integer.parseInt(sanitize(ValueRangeUtils.getValue(valueRanges, 3, 2)));
    }

    public static Number getStatTwo(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, 3, 2)));
    }

    public static Number getStatThree(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, 4, 2)));
    }

    public static boolean isUwPlusLocked(List<ValueRange> valueRanges) {
        return !Boolean.parseBoolean(ValueRangeUtils.getValue(valueRanges, 5, 2));
    }

    public static Number uwPlusStat(List<ValueRange> valueRanges, Function<String, Number> parser) {
        return parser.apply(sanitize(ValueRangeUtils.getValue(valueRanges, 5, 7)));
    }

    public static int stonedInvestedOne(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 2, 12));
    }

    public static int stonedRequiredOne(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 2, 13));
    }

    public static int stonedInvestedTwo(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 3, 12));
    }

    public static int stonedRequiredTwo(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 3, 13));
    }

    public static int stonedInvestedThree(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 4, 12));
    }

    public static int stonedRequiredThree(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 4, 13));
    }

    public static int stonedInvestedUwPlus(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 5, 12));
    }

    public static int stonedRequiredUwPlus(List<ValueRange> valueRanges) {
        return Integer.parseInt(ValueRangeUtils.getValue(valueRanges, 5, 13));
    }

    private static String sanitize(String s) {
        return s.replace("x", "")
                .replace("#", "")
                .replace("s", "")
                .replace("%", "")
                .replace("Locked", "0");
    }
}
