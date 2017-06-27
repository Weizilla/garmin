package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.fetcher.request.FollowTicketRequestFactory;
import com.weizilla.garmin.fetcher.request.GetActivitiesRequestFactory;
import com.weizilla.garmin.fetcher.request.LoginRequestFactory;
import com.weizilla.garmin.fetcher.request.LtLookupRequestFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActivityFetcherTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ActivityFetcher fetcher;
    @Mock
    private FollowTicketRequestFactory followTicketRequestFactory;
    @Mock
    private GetActivitiesRequestFactory getActivitiesRequestFactory;
    @Mock
    private LoginRequestFactory loginRequestFactory;
    @Mock
    private LtLookupRequestFactory ltLookupRequestFactory;

    @Mock
    private HttpRequestBase request;

    @Mock
    private CloseableHttpClient client;

    @Before
    public void setUp() throws Exception
    {
        when(ltLookupRequestFactory.create(any())).thenReturn(request);
        when(ltLookupRequestFactory.isExtractResult()).thenReturn(true);

        when(loginRequestFactory.create(any())).thenReturn(request);
        when(loginRequestFactory.isExtractResult()).thenReturn(true);

        when(getActivitiesRequestFactory.create(any())).thenReturn(request);
        when(getActivitiesRequestFactory.isExtractResult()).thenReturn(true);

        when(followTicketRequestFactory.create(any())).thenReturn(request);
        when(followTicketRequestFactory.isExtractResult()).thenReturn(true);

        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), ltLookupRequestFactory, loginRequestFactory,
            getActivitiesRequestFactory, followTicketRequestFactory, 0, new LogConfig());
    }

    @Test
    public void createsWithDefaultLimit() throws Exception
    {
        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), ltLookupRequestFactory, loginRequestFactory,
            getActivitiesRequestFactory, followTicketRequestFactory, new LogConfig());
        assertThat(fetcher.getRateLimit()).isEqualTo(ActivityFetcher.DEFAULT_RATE_LIMIT_MS);
    }

    @Test
    public void executesRequestByClient() throws Exception
    {
        fetcher.fetch();
        verify(client, times(4)).execute(request);
    }

    @Test
    public void passesResultStringBetweenFactoriesAndReturnLastResult() throws Exception
    {
        String response1 = "RESPONSE 1";
        String response2 = "RESPONSE 2";
        String response3 = "RESPONSE 3";
        String response4 = "RESPONSE 4";

        CloseableHttpResponse httpResponse1 = createResponse(response1);
        CloseableHttpResponse httpResponse2 = createResponse(response2);
        CloseableHttpResponse httpResponse3 = createResponse(response3);
        CloseableHttpResponse httpResponse4 = createResponse(response4);

        when(client.execute(request)).thenReturn(httpResponse1, httpResponse2, httpResponse3, httpResponse4);

        String result = fetcher.fetch();
        verify(ltLookupRequestFactory).create(null);
        verify(loginRequestFactory).create(response1);
        verify(followTicketRequestFactory).create(response2);
        verify(getActivitiesRequestFactory).create(response3);
        assertThat(result).isEqualTo(response4);
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