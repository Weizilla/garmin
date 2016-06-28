package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivitiesFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesFetcher.class);
    private static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<RequestFactory> requestFactories;
    private final int rateLimit;

    public ActivitiesFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories,
        int rateLimit)
    {
        this.clientFactory = clientFactory;
        this.requestFactories = requestFactories;
        this.rateLimit = rateLimit;
    }

    public ActivitiesFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories)
    {
        this(clientFactory, requestFactories, DEFAULT_RATE_LIMIT_MS);
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
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            return factory.isExtractResult() ? EntityUtils.toString(response.getEntity()) : null;
        }
    }
}
