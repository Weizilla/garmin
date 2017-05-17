package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivityDownloader
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDownloader.class);
    private final ActivityParser parser;
    private final ActivityFetcher fetcher;

    public ActivityDownloader(ActivityParser parser, ActivityFetcher fetcher)
    {
        this.parser = parser;
        this.fetcher = fetcher;
    }

    public List<Activity> download() throws Exception
    {
        String json = fetcher.fetch();
        List<Activity> activities = parser.parse(json);
        logger.debug("Downloaded {} activities from Garmin", activities.size());
        return activities;
    }

}
