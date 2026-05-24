package com.pphi.tower.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TowerEra(int major, int minor, int patch) implements Comparable<TowerEra> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

    @JsonCreator
    public static TowerEra parse(String s) {
        if (s == null || s.isBlank()) return new TowerEra(0, 0, 0);
        Matcher m = VERSION_PATTERN.matcher(s);
        if (m.find()) {
            return new TowerEra(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }
        return new TowerEra(0, 0, 0);
    }

    @Override
    public int compareTo(TowerEra other) {
        int c = Integer.compare(this.major, other.major);
        if (c != 0) return c;
        c = Integer.compare(this.minor, other.minor);
        if (c != 0) return c;
        return Integer.compare(this.patch, other.patch);
    }

    @JsonValue
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
