package com.weizilla.garmin.downloader;

import com.weizilla.distance.Distance;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.test.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityParserTest {
    private ActivityParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new ActivityParser();
    }

    @Test
    public void parsesActivitiesFromJsonFile() throws Exception {
        String activitiesJson = TestUtils.readResource("activities.json");
        List<Activity> activities = parser.parse(activitiesJson);
        assertThat(activities).hasSize(1);

        Activity activity = activities.get(0);

        assertThat(activity.getId()).isEqualTo(1493610294);
        assertThat(activity.getType()).isEqualTo("cycling");
        assertThat(activity.getDuration()).isEqualTo(Duration.ofSeconds(5498));
        assertThat(activity.getDistance()).isEqualTo(Distance.ofMeters(38208.37));

        LocalDateTime start = LocalDateTime.of(2016, Month.DECEMBER, 23, 9, 1, 12);
        assertThat(activity.getStart()).isEqualTo(start);
    }

    @Test
    public void parsesVariousFormats() throws Exception {
        Distance.parse("2,300 m");
        Distance.parse("2.3 km");
        Distance.parse("2.3 mi");
        Distance.parse("2.3 yd");
    }

    @Test
    public void returnsEmptyListForNullOrEmptyInput() throws Exception {
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse("         ")).isEmpty();
        assertThat(parser.parse(null)).isEmpty();
    }
}
