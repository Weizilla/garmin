package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.test.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ActivityParserTest
{
    private ActivityParser parser;

    @Before
    public void setUp() throws Exception
    {
        parser = new ActivityParser();
    }

    @Test
    public void parsesActivitiesFromJsonFile() throws Exception
    {
        String activitiesJson = TestUtils.readResource("activities.json");
        List<Activity> activities = parser.parse(activitiesJson);
        assertThat(activities).hasSize(1);

        Activity activity = activities.get(0);
        assertThat(activity.getId()).isEqualTo(1286232939);
        assertThat(activity.getType()).isEqualTo("running");
        assertThat(activity.getDuration()).isEqualTo(Duration.ofSeconds(1527));
        assertThat(activity.getDistance()).isCloseTo(5, within(0.1));
        LocalDateTime start = LocalDateTime.of(2016, Month.AUGUST, 3, 6, 22, 25);
        assertThat(activity.getStart()).isEqualTo(start);
    }

    @Test
    public void returnsEmptyListForNullOrEmptyInput() throws Exception
    {
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse("         ")).isEmpty();
        assertThat(parser.parse(null)).isEmpty();
    }
}