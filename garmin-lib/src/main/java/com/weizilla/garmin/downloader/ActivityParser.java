package com.weizilla.garmin.downloader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weizilla.distance.Distance;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.entity.ImmutableActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Singleton
public class ActivityParser {
    private static final Logger logger = LoggerFactory.getLogger(ActivityParser.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Activity> parse(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode jsonNode = MAPPER.readTree(json);
        Iterator<JsonNode> activities = jsonNode.at("/results/activities").elements();
        return parseActivities(activities);
    }

    private static List<Activity> parseActivities(Iterator<JsonNode> activities) {
        return toStream(activities)
            .map(n -> n.get("activity"))
            .map(ActivityParser::parseActivity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private static <T> Stream<T> toStream(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static Optional<Activity> parseActivity(JsonNode jsonNode) {
        Long activityId = null;
        Activity activity = null;
        try {
            activityId = jsonNode.at("/activityId").asLong();
            String type = jsonNode.at("/activityType/key").asText();

            String distanceStr = jsonNode.at("/activitySummary/SumDistance/withUnitAbbr").asText();
            Distance distance = Distance.parse(distanceStr);

            int seconds = jsonNode.at("/activitySummary/SumDuration/value").asInt();
            Duration duration = Duration.ofSeconds(seconds);

            String startStamp = jsonNode.at("/activitySummary/BeginTimestamp/value").asText();
            Instant startInstant = Instant.parse(startStamp);
            ZoneId startZoneId =
                ZoneId.of(jsonNode.at("/activitySummary/BeginTimestamp/uom").asText());
            LocalDateTime start = LocalDateTime.ofInstant(startInstant, startZoneId);

            activity = ImmutableActivity.builder()
                .id(activityId)
                .type(type)
                .start(start)
                .duration(duration)
                .distance(distance)
                .build();
        } catch (Exception e) {
            logger.error("Error parsing activity {}: {}. Json: {}", activityId, e.getMessage(), jsonNode, e);
        }
        return Optional.ofNullable(activity);
    }
}
