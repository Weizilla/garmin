package com.weizilla.garmin.cli;

import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivitiesFetcher;
import com.weizilla.garmin.parser.ActivitiesParser;
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
public class ActivityDownloader implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDownloader.class);
    @Autowired
    private ActivitiesFetcher fetcher;
    @Autowired
    private ActivitiesParser parser;

    @Override
    public void run(String... strings) throws Exception
    {
        String json = fetcher.fetch();
        List<Activity> activities = parser.parse(json);
        logger.info("Got {} activities", activities.size());
        activities.forEach(a -> logger.info(a.toString()));
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication application = new SpringApplication(ActivityDownloader.class);
        application.setWebEnvironment(false);
        application.run(args);
    }
}

