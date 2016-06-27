package com.weizilla.garmin.fetcher;

import com.google.common.collect.Lists;
import com.weizilla.garmin.fetcher.request.GetActivitiesRequestFactory;
import com.weizilla.garmin.fetcher.request.HeadTicketRequestFactory;
import com.weizilla.garmin.fetcher.request.LoginRequestFactory;
import com.weizilla.garmin.fetcher.request.LtLookupRequestFactory;
import com.weizilla.garmin.fetcher.request.Request;
import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivitiesFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesFetcher.class);
    private static final int RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<RequestFactory> factories;

    public ActivitiesFetcher(HttpClientFactory clientFactory, String username, String password)
    {
        this.clientFactory = clientFactory;
        factories = Lists.newArrayList(
            new LtLookupRequestFactory(), new LoginRequestFactory(username, password),
            new HeadTicketRequestFactory(), new GetActivitiesRequestFactory()
        );
    }

    public String fetch() throws Exception
    {
        String lastResult = null;
        try (CloseableHttpClient client = clientFactory.build())
        {
            for (RequestFactory factory : factories)
            {
                Request request = factory.create(lastResult);
                try (CloseableHttpResponse response = client.execute(request.getHttpRequest()))
                {
                    lastResult = request.isExtractResult() ? EntityUtils.toString(response.getEntity()) : null;
                    if (request.isFinalStep())
                    {
                        return lastResult;
                    }
                }
                Thread.sleep(RATE_LIMIT_MS);
            }
        }
        return lastResult;
    }
}
