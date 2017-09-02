package com.weizilla.garmin.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.weizilla.distance.Distance;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Immutable
@JsonSerialize(as = ImmutableActivity.class)
@JsonDeserialize(as = ImmutableActivity.class)
public interface Activity {
    long getId();

    String getType();

    LocalDateTime getStart();

    @Derived
    default LocalDate getDate() {
        return getStart().toLocalDate();
    }

    Duration getDuration();

    Distance getDistance();
}
