package com.weizilla.garmin.downloader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.weizilla.garmin.entity.Activity;
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

        Injector injector = Guice.createInjector(new ActivityDownloaderModule(args[0], args[1]));
        ActivityDownloader downloader = injector.getInstance(ActivityDownloader.class);
        List<Activity> activities = downloader.download();
        activities.forEach(a -> logger.info(a.toString()));
    }
}

