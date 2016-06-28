package com.weizilla.garmin;

import com.weizilla.garmin.entity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = "com.weizilla.garmin")
public class ActivitiesDownloaderCli implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesDownloaderCli.class);
    @Autowired
    private ActivitiesDownloader downloader;

    @Override
    public void run(String... strings) throws Exception
    {
        List<Activity> activities = downloader.download();
        activities.forEach(a -> logger.info(a.toString()));
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication application = new SpringApplication(ActivitiesDownloaderCli.class);
        application.setWebEnvironment(false);
        application.run(args);
    }
}

