package com.weizilla.garmin.cli;

import com.google.common.collect.Lists;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivitiesFetcher;
import com.weizilla.garmin.fetcher.HttpClientFactory;
import com.weizilla.garmin.fetcher.request.GetActivitiesRequestFactory;
import com.weizilla.garmin.fetcher.request.HeadTicketRequestFactory;
import com.weizilla.garmin.fetcher.request.LoginRequestFactory;
import com.weizilla.garmin.fetcher.request.LtLookupRequestFactory;
import com.weizilla.garmin.fetcher.request.RequestFactory;
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
        List<RequestFactory> requestFactories = Lists.newArrayList(
            new LtLookupRequestFactory(), new LoginRequestFactory(username, password),
            new HeadTicketRequestFactory(), new GetActivitiesRequestFactory()
        );
        HttpClientFactory clientFactory = new HttpClientFactory();
        ActivitiesFetcher fetcher = new ActivitiesFetcher(clientFactory, requestFactories);
        ActivitiesParser parser = new ActivitiesParser();

        String json = fetcher.fetch();
        List<Activity> activities = parser.parse(json);
        activities.forEach(System.out::println);
    }
}
