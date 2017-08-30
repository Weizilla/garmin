package com.weizilla.garmin.distance;

public class DistanceParseException extends RuntimeException {
    public DistanceParseException(String message) {
        super(message);
    }

    public DistanceParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
