package com.weizilla.garmin.parser;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.test.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ActivitiesParserTest
{
    private ActivitiesParser parser;

    @Before
    public void setUp() throws Exception
    {
        parser = new ActivitiesParser();
    }

    @Test
    public void parsesActivityFromJsonFile() throws Exception
    {
        String activitiesJson = TestUtils.readFile("activities.json");
        List<Activity> activities = parser.parse(activitiesJson);
        assertThat(activities).hasSize(1);

        Activity activity = activities.get(0);
        assertThat(activity.getId()).isEqualTo(1211725706);
        assertThat(activity.getType()).isEqualTo("running");
        assertThat(activity.getDuration()).isEqualTo(Duration.ofSeconds(4443));
        assertThat(activity.getDistance()).isCloseTo(11.34588, within(0.1));
        assertThat(activity.getStart()).isEqualTo(Instant.parse("2016-06-12T11:56:05.000Z"));
    }
}