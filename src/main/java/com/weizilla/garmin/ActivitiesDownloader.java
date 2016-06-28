package com.weizilla.garmin;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivitiesFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivitiesDownloader
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesDownloader.class);
    private final ActivitiesParser parser;
    private final ActivitiesFetcher fetcher;

    @Autowired
    public ActivitiesDownloader(ActivitiesParser parser, ActivitiesFetcher fetcher)
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
