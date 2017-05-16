package com.weizilla.garmin.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Activity
{
    @Id
    private final long id;
    private final String type;
    private final Duration duration;
    private final LocalDateTime start;
    private final Quantity<Length> distance;

    @PersistenceConstructor
    @JsonCreator
    public Activity(@JsonProperty("id") long id,
        @JsonProperty("type") String type,
        @JsonProperty("duration") Duration duration,
        @JsonProperty("start") LocalDateTime start,
        @JsonProperty("distance") Quantity<Length> distance)
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

    public LocalDateTime getStart()
    {
        return start;
    }

    public Quantity<Length> getDistance()
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Activity activity = (Activity) o;
        return id == activity.id &&
            Objects.equals(activity.distance, distance) &&
            Objects.equals(type, activity.type) &&
            Objects.equals(duration, activity.duration) &&
            Objects.equals(start, activity.start);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, type, duration, start, distance);
    }
}
