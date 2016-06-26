package com.weizilla.garmin.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weizilla.garmin.entity.Activity;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ActivitiesParser
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final double MILES_TO_KM = 1.60934;

    static
    {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Activity> parse(String json) throws IOException
    {
        JsonNode jsonNode = MAPPER.readTree(json);
        Iterator<JsonNode> activities = jsonNode.at("/results/activities").elements();
        return toStream(activities)
            .map(n -> n.get("activity"))
            .map(ActivitiesParser::parse)
            .collect(Collectors.toList());
    }

    private static <T> Stream<T> toStream(Iterator<T> iterator)
    {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static Activity parse(JsonNode jsonNode)
    {
        long activityId = jsonNode.at("/activityId").asLong();
        String type = jsonNode.at("/activityType/key").asText();
        double distance = jsonNode.at("/activitySummary/SumDistance/value").asDouble() * MILES_TO_KM;

        int seconds = jsonNode.at("/activitySummary/SumDuration/value").asInt();
        Duration duration = Duration.ofSeconds(seconds);

        String startStamp = jsonNode.at("/activitySummary/BeginTimestamp/value").asText();
        Instant start = Instant.parse(startStamp);

        return new Activity(activityId, type, duration, start, distance);
    }
}
