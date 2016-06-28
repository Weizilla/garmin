package com.weizilla.garmin.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.Duration;
import java.time.Instant;

public class Activity
{
    @Id
    private final long id;
    private final String type;
    private final Duration duration;
    private final Instant start;
    private final double distance;

    @PersistenceConstructor
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

    @Override
    public String toString()
    {
        return "Activity{" +
            "id=" + id +
            ", type='" + type + '\'' +
            ", duration=" + duration +
            ", start=" + start +
            ", distance=" + distance +
            '}';
    }
}
