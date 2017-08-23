package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.fetcher.step.FollowTicketStep;
import com.weizilla.garmin.fetcher.step.GetActivitiesStep;
import com.weizilla.garmin.fetcher.step.LoginStep;
import com.weizilla.garmin.fetcher.step.LtLookupStep;
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

public class ActivityFetcherTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ActivityFetcher fetcher;
    @Mock
    private FollowTicketStep followTicketStep;
    @Mock
    private GetActivitiesStep getActivitiesStep;
    @Mock
    private LoginStep loginStep;
    @Mock
    private LtLookupStep ltLookupStep;
    @Mock
    private HttpRequestBase request;
    @Mock
    private CloseableHttpClient client;

    @Before
    public void setUp() throws Exception {
        when(ltLookupStep.create(any())).thenReturn(request);
        when(ltLookupStep.isExtractResult()).thenReturn(true);

        when(loginStep.create(any())).thenReturn(request);
        when(loginStep.isExtractResult()).thenReturn(true);

        when(getActivitiesStep.create(any())).thenReturn(request);
        when(getActivitiesStep.isExtractResult()).thenReturn(true);

        when(followTicketStep.create(any())).thenReturn(request);
        when(followTicketStep.isExtractResult()).thenReturn(true);

        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), ltLookupStep,
            loginStep,
            getActivitiesStep, followTicketStep, 0, new LogConfig());
    }

    @Test
    public void createsWithDefaultLimit() throws Exception {
        fetcher = new ActivityFetcher(new HttpClientFactoryStub(), ltLookupStep,
            loginStep,
            getActivitiesStep, followTicketStep, new LogConfig());
        assertThat(fetcher.getRateLimit()).isEqualTo(ActivityFetcher.DEFAULT_RATE_LIMIT_MS);
    }

    @Test
    public void executesRequestByClient() throws Exception {
        fetcher.fetch();
        verify(client, times(4)).execute(request);
    }

    @Test
    public void passesResultStringBetweenFactoriesAndReturnLastResult() throws Exception {
        String response1 = "RESPONSE 1";
        String response2 = "RESPONSE 2";
        String response3 = "RESPONSE 3";
        String response4 = "RESPONSE 4";

        CloseableHttpResponse httpResponse1 = createResponse(response1);
        CloseableHttpResponse httpResponse2 = createResponse(response2);
        CloseableHttpResponse httpResponse3 = createResponse(response3);
        CloseableHttpResponse httpResponse4 = createResponse(response4);

        when(client.execute(request))
            .thenReturn(httpResponse1, httpResponse2, httpResponse3, httpResponse4);

        String result = fetcher.fetch();
        verify(ltLookupStep).create(null);
        verify(loginStep).create(response1);
        verify(followTicketStep).create(response2);
        verify(getActivitiesStep).create(response3);
        assertThat(result).isEqualTo(response4);
    }

    private static CloseableHttpResponse createResponse(String response) throws Exception {
        HttpEntity entity = new StringEntity(response);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(entity);
        return httpResponse;
    }

    private class HttpClientFactoryStub extends HttpClientFactory {
        @Override
        public CloseableHttpClient build() {
            return client;
        }
    }
}