package com.weizilla.garmin.downloader;

import com.weizilla.garmin.entity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class ActivityDownloaderCli implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDownloaderCli.class);
    private final ActivityDownloader downloader;

    @Autowired
    public ActivityDownloaderCli(ActivityDownloader downloader)
    {
        this.downloader = downloader;
    }

    @Override
    public void run(String... strings) throws Exception
    {
        List<Activity> activities = downloader.download();
        activities.forEach(a -> logger.info(a.toString()));
    }
}

