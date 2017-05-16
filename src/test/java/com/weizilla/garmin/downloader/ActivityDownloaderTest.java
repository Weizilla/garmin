package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
public class ActivityDownloaderTest
{
    @Mock
    private ActivityFetcher fetcher;
    @Mock
    private ActivityParser parser;
    private ActivityDownloader downloader;

    @Before
    public void setUp() throws Exception
    {
        downloader = new ActivityDownloader(parser, fetcher);
    }

    @Test
    public void parseFetchedJson() throws Exception
    {
        String json = "JSON";
        when(fetcher.fetch()).thenReturn(json);
        downloader.download();
        verify(parser).parse(json);
    }

    @Test
    public void returnsParsedActivities() throws Exception
    {
        Activity activity = new Activity(1, "TYPE", Duration.ofDays(1), LocalDateTime.now(),
            Quantities.getQuantity(1, USCustomary.MILE));
        List<Activity> activities = Collections.singletonList(activity);
        when(parser.parse(anyString())).thenReturn(activities);

        List<Activity> results = downloader.download();
        assertThat(results).isSameAs(activities);
    }
}