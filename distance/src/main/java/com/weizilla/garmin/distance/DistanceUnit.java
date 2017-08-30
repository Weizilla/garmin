package com.weizilla.garmin.distance;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public enum DistanceUnit {
    KILOMETER(Distance::ofKilometers, "km", "kilometer", "kilometers"),
    MILE(Distance::ofMiles, "mi", "mile", "miles"),
    METER(Distance::ofMeters, "m", "meter", "meters"),
    YARD(Distance::ofYards, "yd", "yard", "yards"),
    FEET(Distance::ofFeet, "ft", "foot", "feet");

    private static final Map<String, DistanceUnit> UNIT_STRINGS = new HashMap<>();

    static {
        for (DistanceUnit unit : DistanceUnit.values()) {
            for (String string : unit.getStrings()) {
                UNIT_STRINGS.put(string, unit);
            }
        }
    }

    private final Function<Double, Distance> creator;
    private final Set<String> strings;

    DistanceUnit(Function<Double, Distance> creator, String...strings) {
        this.creator = creator;
        this.strings = Sets.newHashSet(strings);
    }

    public Function<Double, Distance> getCreator() {
        return creator;
    }

    public Set<String> getStrings() {
        return strings;
    }

    public static DistanceUnit parse(String string) {
        DistanceUnit unit = UNIT_STRINGS.get(string.trim().toLowerCase());
        if (unit == null) {
            throw new DistanceParseException("Unable to find unit: " + string);
        }
        return unit;
    }
}
