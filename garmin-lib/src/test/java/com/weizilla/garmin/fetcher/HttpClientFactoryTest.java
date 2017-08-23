package com.weizilla.garmin.fetcher;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientFactoryTest {
    private HttpClientFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new HttpClientFactory();
    }

    @Test
    public void createsClientWithCookieStore() throws Exception {
        try (CloseableHttpClient client = factory.build()) {
            assertThat(client).isNotNull();
        }
    }
}