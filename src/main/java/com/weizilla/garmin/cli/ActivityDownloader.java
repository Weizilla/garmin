package com.weizilla.garmin.cli;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivitiesFetcher;
import com.weizilla.garmin.fetcher.HttpClientFactory;
import com.weizilla.garmin.parser.ActivitiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivityDownloader
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDownloader.class);

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            logger.warn("Arguments: USERNAME PASSWORD");
            System.exit(1);
        }

        printActivities(args[0], args[1]);
    }

    private static void printActivities(String username, String password) throws Exception
    {
        HttpClientFactory factory = new HttpClientFactory();
        ActivitiesFetcher fetcher = new ActivitiesFetcher(factory, username, password);
        ActivitiesParser parser = new ActivitiesParser();

        String json = fetcher.fetch();
        List<Activity> activities = parser.parse(json);
        activities.forEach(System.out::println);
    }
}
