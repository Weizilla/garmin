package com.weizilla.garmin.distance;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class Distance {
    public static final double MILE_TO_METER = 1609.34;
    public static final double YARD_TO_METER = 0.9144;
    public static final double FEET_TO_METER = 0.3048;
    private static final String PARSE_REGEX = "([\\d.,]+)\\s*([\\w]+)";
    private static final Pattern PARSE_PATTERN = Pattern.compile(PARSE_REGEX, CASE_INSENSITIVE);
    private static final Pattern REMOVE_COMMA = Pattern.compile(",", Pattern.LITERAL);
    private final long distanceMeter;

    private Distance(double distanceMeter) {
        this.distanceMeter = Math.round(distanceMeter);
    }

    public static Distance of(double value, DistanceUnit unit) {
        return unit.getCreator().apply(value);
    }

    public static Distance ofMeters(double meters) {
        return new Distance(meters);
    }

    public static Distance ofKilometers(double kilometers) {
        return new Distance(kilometers * 1000);
    }

    public static Distance ofMiles(double miles) {
        return new Distance(miles * MILE_TO_METER);
    }

    public static Distance ofYards(double yards) {
        return new Distance(yards * YARD_TO_METER);
    }

    public static Distance ofFeet(double feet) {
        return new Distance(feet * FEET_TO_METER);
    }

    public static Distance parse(CharSequence text) {
        Matcher matcher = PARSE_PATTERN.matcher(text);
        if (matcher.find()) {
            double value = parseValue(matcher.group(1));
            DistanceUnit unit = DistanceUnit.parse(matcher.group(2));
            return of(value, unit);
        } else {
            throw new DistanceParseException("Unable to parse distance: " + text);
        }
    }

    private static double parseValue(String text) {
        double value;
        try {
            value = Double.valueOf(REMOVE_COMMA.matcher(text).replaceAll(""));
        } catch (NumberFormatException e) {
            throw new DistanceParseException("Unable to parse value for distance: " + text, e);
        }
        return value;
    }

    public Distance plus(Distance distance) {
        return new Distance(distanceMeter + distance.distanceMeter);
    }

    public Distance minus(Distance distance) {
        return new Distance(distanceMeter - distance.distanceMeter);
    }

    public Distance multipliedBy(double multiplicand) {
        return new Distance(distanceMeter * multiplicand);
    }

    public Distance dividedBy(double divisor) {
        return new Distance(distanceMeter / divisor);
    }

    public double dividedBy(Distance divisor) {
        return (double) distanceMeter / divisor.distanceMeter;
    }

    public long getDistanceMeter() {
        return distanceMeter;
    }

    @Override
    public String toString() {
        return distanceMeter + " m";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return distanceMeter == distance.distanceMeter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distanceMeter);
    }
}
