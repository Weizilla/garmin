package com.weizilla.garmin.entity;

import java.time.Duration;
import java.time.Instant;

public class Activity
{
    private final long id;
    private final String type;
    private final Duration duration;
    private final Instant start;
    private final double distance;

    public Activity(long id, String type, Duration duration, Instant start, double distance)
    {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.start = start;
        this.distance = distance;
    }

    public long getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public Instant getStart()
    {
        return start;
    }

    public double getDistance()
    {
        return distance;
    }
}
