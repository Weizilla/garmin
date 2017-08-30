package com.weizilla.garmin.distance;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

import static com.weizilla.garmin.distance.DistanceUnit.FEET;
import static com.weizilla.garmin.distance.DistanceUnit.KILOMETER;
import static com.weizilla.garmin.distance.DistanceUnit.METER;
import static com.weizilla.garmin.distance.DistanceUnit.MILE;
import static com.weizilla.garmin.distance.DistanceUnit.YARD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DistanceParseTest {
    private final String text;
    private final double expectedValue;
    private final DistanceUnit expectedUnit;

    @Parameters(name = "{index}: parse({0})={1} {2}")
    public static Collection<Object[]> parameters() {
        return Lists.newArrayList(
            new Object[]{"100 km", 100, KILOMETER},
            new Object[]{"10.55 km", 10.55, KILOMETER},
            new Object[]{"100 kilometer", 100, KILOMETER},
            new Object[]{"1,000 km", 1000, KILOMETER},

            new Object[]{"100 mi", 100, MILE},
            new Object[]{"10.55 mi", 10.55, MILE},
            new Object[]{"100 mile", 100, MILE},
            new Object[]{"100 miles", 100, MILE},
            new Object[]{"1,000 mi", 1000, MILE},

            new Object[]{"100 m", 100, METER},
            new Object[]{"10.55 m", 10.55, METER},
            new Object[]{"100 meter", 100, METER},
            new Object[]{"100 meters", 100, METER},
            new Object[]{"1,000 m", 1000, METER},
            new Object[]{"1,000 meter", 1000, METER},

            new Object[]{"100 yd", 100, YARD},
            new Object[]{"10.55 yd", 10.55, YARD},
            new Object[]{"100 yard", 100, YARD},
            new Object[]{"100 yards", 100, YARD},
            new Object[]{"1,000 yd", 1000, YARD},

            new Object[]{"100 ft", 100, FEET},
            new Object[]{"10.55 ft", 10.55, FEET},
            new Object[]{"100 foot", 100, FEET},
            new Object[]{"100 feet", 100, FEET},
            new Object[]{"1,000 ft", 1000, FEET}
        );
    }

    public DistanceParseTest(String text, double expectedValue, DistanceUnit expectedUnit) {
        this.text = text;
        this.expectedValue = expectedValue;
        this.expectedUnit = expectedUnit;
    }

    @Test
    public void parsingTest() throws Exception {
        Distance distance = Distance.parse(text);
        assertThat(distance).isNotNull();

        Distance expected = Distance.of(expectedValue, expectedUnit);
        assertThat(expected).isNotNull();

        assertThat(distance).isEqualTo(expected);
    }

    @Test
    public void parsingTestUppercase() throws Exception {
        Distance distance = Distance.parse(text.toUpperCase());
        assertThat(distance).isNotNull();

        Distance expected = Distance.of(expectedValue, expectedUnit);
        assertThat(expected).isNotNull();

        assertThat(distance).isEqualTo(expected);
    }
}