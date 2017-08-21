package com.weizilla.garmin.downloader;

import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.configuration.UrlBases;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import com.weizilla.garmin.fetcher.HttpClientFactory;
import com.weizilla.garmin.fetcher.request.FollowTicketRequestFactory;
import com.weizilla.garmin.fetcher.request.GetActivitiesRequestFactory;
import com.weizilla.garmin.fetcher.request.LoginRequestFactory;
import com.weizilla.garmin.fetcher.request.LtLookupRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivityDownloaderRunner
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDownloaderRunner.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            logger.error("Require USERNAME PASSWORD");
            System.exit(1);
        }

        UrlBases bases = new UrlBases();
        bases.setFollowTicket("https://connect.garmin.com");
        bases.setGetActivities("https://connect.garmin.com");
        bases.setLogin("https://sso.garmin.com");
        bases.setLtLookup("https://sso.garmin.com");

        LogConfig logConfig = new LogConfig();
        logConfig.setResult(false);
        logConfig.setUrl(true);

        Credentials credentials = new Credentials();
        credentials.setUsername(args[0]);
        credentials.setPassword(args[1]);

        ActivityParser parser = new ActivityParser();

        HttpClientFactory httpClientFactory = new HttpClientFactory();
        LtLookupRequestFactory ltLookupRequestFactory = new LtLookupRequestFactory(bases);
        LoginRequestFactory loginRequestFactory = new LoginRequestFactory(bases, credentials);
        FollowTicketRequestFactory followTicketRequestFactory = new FollowTicketRequestFactory(bases);
        GetActivitiesRequestFactory getActivitiesRequestFactory = new GetActivitiesRequestFactory(bases);
        ActivityFetcher fetcher = new ActivityFetcher(httpClientFactory, ltLookupRequestFactory,
            loginRequestFactory, getActivitiesRequestFactory, followTicketRequestFactory, logConfig);
        ActivityDownloader downloader = new ActivityDownloader(parser, fetcher);
        List<Activity> activities = downloader.download();
        activities.forEach(a -> logger.info(a.toString()));
    }
}

