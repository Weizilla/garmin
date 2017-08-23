package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.entity.ImmutableActivity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import systems.uom.common.USCustomary;
import tec.uom.se.quantity.Quantities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActivityDownloaderTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String JSON = "{}";
    @Mock
    private ActivityFetcher fetcher;
    @Mock
    private ActivityParser parser;
    private ActivityDownloader downloader;

    @Before
    public void setUp() throws Exception
    {
        downloader = new ActivityDownloader(parser, fetcher);
        when(fetcher.fetch()).thenReturn(JSON);
    }

    @Test
    public void parseFetchedJson() throws Exception
    {
        downloader.download();
        verify(parser).parse(JSON);
    }

    @Test
    public void returnsParsedActivities() throws Exception
    {
        Activity activity = ImmutableActivity.builder()
            .id(1)
            .type("TYPE")
            .duration(Duration.ofDays(1))
            .start(LocalDateTime.now())
            .distance(Quantities.getQuantity(1, USCustomary.MILE))
            .build();
        List<Activity> activities = Collections.singletonList(activity);
        when(parser.parse(anyString())).thenReturn(activities);

        List<Activity> results = downloader.download();
        assertThat(results).isSameAs(activities);
    }
}