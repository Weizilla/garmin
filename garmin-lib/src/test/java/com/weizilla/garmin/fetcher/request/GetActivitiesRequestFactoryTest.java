package com.weizilla.garmin.fetcher.request;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetActivitiesRequestFactoryTest
{
    private GetActivitiesRequestFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = new GetActivitiesRequestFactory(new UrlBases());
    }

    @Test
    public void returnsHttpRequest() throws Exception
    {
        HttpUriRequest request = factory.create(null);
        assertThat(request.getURI().toString()).contains(GetActivitiesRequestFactory.GET_ACTIVITIES_URL);
    }

    @Test
    public void extractsResult() throws Exception
    {
        assertThat(factory.isExtractResult()).isTrue();
    }
}