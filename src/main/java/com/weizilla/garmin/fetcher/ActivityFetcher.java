package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<RequestFactory> requestFactories;
    private final int rateLimit;
    private final boolean printResponse;

    @Autowired
    public ActivityFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories,
        @Value("${printResponse:false}") boolean printResponse)
    {
        this(clientFactory, requestFactories, DEFAULT_RATE_LIMIT_MS, printResponse);
    }

    ActivityFetcher(HttpClientFactory clientFactory, List<RequestFactory> requestFactories,
        int rateLimit, boolean printResponse)
    {
        this.clientFactory = clientFactory;
        this.requestFactories = requestFactories;
        this.rateLimit = rateLimit;
        this.printResponse = printResponse;
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
        logger.info("Executing request factory: {}", factory.getClass().getSimpleName());
        HttpUriRequest request = factory.create(lastResult);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            String result = factory.isExtractResult() ? EntityUtils.toString(response.getEntity()) : null;
            if (printResponse)
            {
                logger.info(result);
            }
            return result;
        }
    }

    protected int getRateLimit()
    {
        return rateLimit;
    }
}
