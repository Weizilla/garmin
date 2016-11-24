package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.LogConfig;
import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ActivityFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<RequestFactory> requestFactories;
    private final int rateLimit;
    private final LogConfig logConfig;

    @Autowired
    public ActivityFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories,
        LogConfig logConfig)
    {
        this(clientFactory, requestFactories, DEFAULT_RATE_LIMIT_MS, logConfig);
    }

    ActivityFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories,
        int rateLimit, LogConfig logConfig)
    {
        this.clientFactory = clientFactory;
        this.requestFactories = requestFactories;
        this.rateLimit = rateLimit;
        this.logConfig = logConfig;
    }

    public String fetch() throws Exception
    {
        String lastResult = null;
        try (CloseableHttpClient client = clientFactory.build())
        {
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
            String result = factory.isExtractResult() ? EntityUtils.toString(response.getEntity()) : null;
            logResult(factory, result);
            return result;
        }
    }

    private void logRequest(RequestFactory factory, HttpUriRequest request) throws IOException
    {
        if (logConfig.isUrl())
        {
            logger.info("Executing step {}: {} {}",
                factory.getStepName(), request.getMethod(), request.getURI());
        }
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
