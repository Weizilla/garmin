package com.weizilla.garmin.fetcher;

import com.google.common.collect.Lists;
import com.weizilla.garmin.fetcher.step.FetchStep;
import com.weizilla.garmin.fetcher.step.GetActivities;
import com.weizilla.garmin.fetcher.step.HeadTicket;
import com.weizilla.garmin.fetcher.step.Login;
import com.weizilla.garmin.fetcher.step.LtLookup;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivitiesFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesFetcher.class);
    private static final int RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final String username;
    private final String password;

    public ActivitiesFetcher(HttpClientFactory clientFactory, String username, String password)
    {
        this.clientFactory = clientFactory;
        this.username = username;
        this.password = password;
    }

    public String fetch() throws Exception
    {
        List<FetchStep> steps = Lists.newArrayList(
            new LtLookup(), new Login(username, password),
            new HeadTicket(), new GetActivities());

        String lastResult = null;
        try (CloseableHttpClient client = clientFactory.build())
        {
            for (FetchStep step : steps)
            {
                logger.info("Executing step {}", step.getClass().getSimpleName());
                lastResult = step.execute(lastResult, client);
                Thread.sleep(RATE_LIMIT_MS);
            }
        }
        return lastResult;
    }
}
