package com.weizilla.garmin.distance;

import org.junit.Test;

import static com.weizilla.garmin.distance.DistanceUnit.FEET;
import static com.weizilla.garmin.distance.DistanceUnit.KILOMETER;
import static com.weizilla.garmin.distance.DistanceUnit.METER;
import static com.weizilla.garmin.distance.DistanceUnit.MILE;
import static com.weizilla.garmin.distance.DistanceUnit.YARD;
import static org.assertj.core.api.Assertions.assertThat;

public class DistanceTest {
    @Test
    public void createsWithMeter() throws Exception {
        long meter = 100;
        Distance distance = Distance.ofMeters(meter);
        assertThat(distance.getDistanceMeter()).isEqualTo(meter);
        distance = Distance.of(meter, METER);
        assertThat(distance.getDistanceMeter()).isEqualTo(meter);
    }

    @Test
    public void createsWithKilometer() throws Exception {
        long km = 100;
        long m = 1000 * km;
        Distance distance = Distance.ofKilometers(km);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));

        distance = Distance.of(km, KILOMETER);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));
    }

    @Test
    public void createsWithMile() throws Exception {
        double mi = 100;
        long m = 160934;
        Distance distance = Distance.ofMiles(mi);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));

        distance = Distance.of(mi, MILE);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));
    }

    @Test
    public void createsWithYards() throws Exception {
        double yd = 100;
        long m = 91;
        Distance distance = Distance.ofYards(yd);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));

        distance = Distance.of(yd, YARD);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));
    }

    @Test
    public void createsWithFeet() throws Exception {
        double ft = 100;
        long m = 30;
        Distance distance = Distance.ofFeet(ft);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));

        distance = Distance.of(ft, FEET);
        assertThat(distance.getDistanceMeter()).isEqualTo(m);
        assertThat(distance).isEqualTo(Distance.ofMeters(m));
    }

    @Test
    public void roundTripParsing() throws Exception {
        Distance distance = Distance.ofMeters(100);
        assertThat(distance).isNotNull();

        Distance roundTrip = Distance.parse(distance.toString());
        assertThat(roundTrip).isNotNull();

        assertThat(distance).isEqualTo(roundTrip);
    }

    @Test
    public void addition() throws Exception {
        Distance a = Distance.ofMeters(10);
        Distance b = Distance.ofMeters(20);
        Distance expected = Distance.ofMeters(30);
        assertThat(a.plus(b)).isEqualTo(expected);
    }

    @Test
    public void subtraction() throws Exception {
        Distance a = Distance.ofMeters(30);
        Distance b = Distance.ofMeters(20);
        Distance expected = Distance.ofMeters(10);
        assertThat(a.minus(b)).isEqualTo(expected);
    }

    @Test
    public void multiplication() throws Exception {
        Distance a = Distance.ofMeters(10);
        double b = 2.5;
        Distance expected = Distance.ofMeters(25);
        assertThat(a.multipliedBy(b)).isEqualTo(expected);
    }

    @Test
    public void divisionByDecimal() throws Exception {
        Distance a = Distance.ofMeters(10);
        double b = 2;
        Distance expected = Distance.ofMeters(5);
        assertThat(a.dividedBy(b)).isEqualTo(expected);
    }

    @Test
    public void divisionByDistance() throws Exception {
        Distance a = Distance.ofMeters(10);
        Distance b = Distance.ofMeters(20);
        double expected = 0.5;
        assertThat(a.dividedBy(b)).isEqualTo(expected);
    }

    @Test(expected = DistanceParseException.class)
    public void throwsErrorWithBadParsing() throws Exception {
        Distance.parse("BAD INPUT");
    }

    @Test(expected = DistanceParseException.class)
    public void throwsErrorWithBadParsingTooManyDecimals() throws Exception {
        Distance.parse("2.2.3 m");
    }

    @Test(expected = DistanceParseException.class)
    public void throwsErrorWithBadParsingNoUnit() throws Exception {
        Distance.parse("2.2");
    }

    @Test(expected = DistanceParseException.class)
    public void throwsErrorWithBadParsingNoValue() throws Exception {
        Distance.parse("meter");
    }
}