package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LtLookupRequestFactoryTest
{
    private LtLookupRequestFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = new LtLookupRequestFactory();
    }

    @Test
    public void returnsHttpRequest() throws Exception
    {
        HttpUriRequest request = factory.create(null);
        assertThat(request.getURI().toString()).contains(LtLookupRequestFactory.LOGIN_URL);
    }

    @Test
    public void extractsResult() throws Exception
    {
        assertThat(factory.isExtractResult()).isTrue();
    }
}