package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.LogConfig;
import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityFetcherTest
{
    private ActivityFetcher fetcher;
    private List<RequestFactory> factories;

    @Mock
    private HttpRequestBase request;

    @Mock
    private CloseableHttpClient client;

    @Before
    public void setUp() throws Exception
    {
        factories = new ArrayList<>();
        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), factories, 0, new LogConfig());
    }

    @Test
    public void createsWithDefaultLimit() throws Exception
    {
        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), factories, new LogConfig());
        assertThat(fetcher.getRateLimit()).isEqualTo(ActivityFetcher.DEFAULT_RATE_LIMIT_MS);
    }

    @Test
    public void executesRequestByClient() throws Exception
    {
        factories.add(new RequestFactoryStub(request, false));
        fetcher.fetch();
        verify(client).execute(request);
    }

    @Test
    public void passesResultStringBetweenFactoriesAndReturnLastResult() throws Exception
    {
        String response1 = "RESPONSE 1";
        String response2 = "RESPONSE 2";
        String response3 = "RESPONSE 3";

        CloseableHttpResponse httpResponse1 = createResponse(response1);
        CloseableHttpResponse httpResponse2 = createResponse(response2);
        CloseableHttpResponse httpResponse3 = createResponse(response3);

        when(client.execute(request)).thenReturn(httpResponse1, httpResponse2, httpResponse3);

        RequestFactory factory1 = spy(new RequestFactoryStub(request, true));
        RequestFactory factory2 = spy(new RequestFactoryStub(request, true));
        RequestFactory factory3 = spy(new RequestFactoryStub(request, true));

        factories.add(factory1);
        factories.add(factory2);
        factories.add(factory3);

        String result = fetcher.fetch();
        verify(factory1).create(null);
        verify(factory2).create(response1);
        verify(factory3).create(response2);
        assertThat(result).isEqualTo(response3);
    }

    private static CloseableHttpResponse createResponse(String response) throws Exception
    {
        HttpEntity entity = new StringEntity(response);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(entity);
        return httpResponse;
    }

    private class HttpClientFactoryStub extends HttpClientFactory
    {
        @Override
        public CloseableHttpClient build()
        {
            return client;
        }
    }
}