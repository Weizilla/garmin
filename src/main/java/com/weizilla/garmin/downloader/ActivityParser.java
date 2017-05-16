package com.weizilla.garmin.downloader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weizilla.garmin.entity.Activity;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class ActivityParser
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final double MILES_TO_KM = 1.60934;

    static
    {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Activity> parse(String json) throws IOException
    {
        if (json == null || json.trim().isEmpty())
        {
            return Collections.emptyList();
        }

        JsonNode jsonNode = MAPPER.readTree(json);
        Iterator<JsonNode> activities = jsonNode.at("/results/activities").elements();
        return parseActivities(activities);
    }

    private static List<Activity> parseActivities(Iterator<JsonNode> activities)
    {
        return toStream(activities)
            .map(n -> n.get("activity"))
            .map(ActivityParser::parseActivity)
            .collect(Collectors.toList());
    }

    private static <T> Stream<T> toStream(Iterator<T> iterator)
    {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static Activity parseActivity(JsonNode jsonNode)
    {
        long activityId = jsonNode.at("/activityId").asLong();
        String type = jsonNode.at("/activityType/key").asText();
        
        String distanceStr = jsonNode.at("/activitySummary/SumDistance/withUnitAbbr").asText();
        Quantity<Length> distance = Quantities.getQuantity(distanceStr).asType(Length.class);

        int seconds = jsonNode.at("/activitySummary/SumDuration/value").asInt();
        Duration duration = Duration.ofSeconds(seconds);

        String startStamp = jsonNode.at("/activitySummary/BeginTimestamp/value").asText();
        Instant startInstant = Instant.parse(startStamp);
        ZoneId startZoneId = ZoneId.of(jsonNode.at("/activitySummary/BeginTimestamp/uom").asText());
        LocalDateTime start = LocalDateTime.ofInstant(startInstant, startZoneId);

        return new Activity(activityId, type, duration, start, distance);
    }
}
