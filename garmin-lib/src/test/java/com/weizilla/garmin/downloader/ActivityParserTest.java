package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Length;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static systems.uom.common.USCustomary.MILE;

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

        assertThat(activity.getId()).isEqualTo(1286232939);
        assertThat(activity.getType()).isEqualTo("running");
        assertThat(activity.getDuration()).isEqualTo(Duration.ofSeconds(1527));

        ComparableQuantity<Length> distance =
            Quantities.getQuantity(BigDecimal.valueOf(3.12), MILE);
        assertThat(activity.getDistance()).isEqualTo(distance);

        LocalDateTime start = LocalDateTime.of(2016, Month.AUGUST, 3, 6, 22, 25);
        assertThat(activity.getStart()).isEqualTo(start);
    }

    @Test
    public void parsesVariousFormats() throws Exception {
        ActivityParser.parseDistance("2,300 m");
        ActivityParser.parseDistance("2.3 km");
        ActivityParser.parseDistance("2.3 mi");
        ActivityParser.parseDistance("2.3 yd");
    }

    @Test
    public void returnsEmptyListForNullOrEmptyInput() throws Exception {
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse("         ")).isEmpty();
        assertThat(parser.parse(null)).isEmpty();
    }
}