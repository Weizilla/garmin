package com.weizilla.garmin.fetcher;

import com.google.common.collect.Lists;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.fetcher.request.FollowTicketRequestFactory;
import com.weizilla.garmin.fetcher.request.GetActivitiesRequestFactory;
import com.weizilla.garmin.fetcher.request.LoginRequestFactory;
import com.weizilla.garmin.fetcher.request.LtLookupRequestFactory;
import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class ActivityFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<RequestFactory> requestFactories;
    private final int rateLimit;
    private final LogConfig logConfig;

    @Inject
    public ActivityFetcher(HttpClientFactory clientFactory,
        LtLookupRequestFactory ltLookupRequestFactory,
        LoginRequestFactory loginRequestFactory,
        GetActivitiesRequestFactory getActivitiesRequestFactory,
        FollowTicketRequestFactory followTicketRequestFactory,
        LogConfig logConfig) {

        this(clientFactory, ltLookupRequestFactory, loginRequestFactory,
            getActivitiesRequestFactory, followTicketRequestFactory, DEFAULT_RATE_LIMIT_MS, logConfig);
    }

    public ActivityFetcher(HttpClientFactory clientFactory, LtLookupRequestFactory ltLookupRequestFactory,
        LoginRequestFactory loginRequestFactory, GetActivitiesRequestFactory getActivitiesRequestFactory,
        FollowTicketRequestFactory followTicketRequestFactory, int rateLimit, LogConfig logConfig) {

        this.clientFactory = clientFactory;
        requestFactories = Lists.newArrayList(ltLookupRequestFactory, loginRequestFactory,
            followTicketRequestFactory, getActivitiesRequestFactory);
        this.rateLimit = rateLimit;
        this.logConfig = logConfig;
    }

    public String fetch() throws Exception
    {
        String lastResult = null;
        try (CloseableHttpClient client = clientFactory.build())
        {
            //TODO refactor out this loop as request factories are always the same and in the
            // same order
            for (RequestFactory factory : requestFactories)
            {
                lastResult = execute(factory, lastResult, client);
            }
        }
        return lastResult;
    }

    private String execute(RequestFactory factory, String lastResult, CloseableHttpClient httpClient)
        throws Exception
    {
        HttpUriRequest request = factory.create(lastResult);
        logRequest(factory, request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            String result = factory.isExtractResult() && response != null ? EntityUtils.toString(response.getEntity()) : null;
            logResult(factory, result);
            return result;
        }
    }

    private void logRequest(RequestFactory factory, HttpUriRequest request) throws IOException
    {
        String url = logConfig.isUrl() ? request.getMethod() + " " + request.getURI() : "";
        logger.info("Executing step [{}] {}", factory.getStepName(), url);
    }

    private void logResult(RequestFactory factory, String result)
    {
        if (logConfig.isResult())
        {
            logger.info("Result for {}:\n{}", factory.getStepName(), result);
        }
    }

    protected int getRateLimit()
    {
        return rateLimit;
    }
}
