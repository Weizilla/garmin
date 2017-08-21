package com.weizilla.garmin.fetcher;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import javax.inject.Singleton;

@Singleton
public class HttpClientFactory
{
    public CloseableHttpClient build()
    {
        RequestConfig config = RequestConfig.custom()
            .setCircularRedirectsAllowed(true)
            .setRedirectsEnabled(true)
            .build();

        return HttpClients.custom()
            .setDefaultCookieStore(new BasicCookieStore())
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setDefaultRequestConfig(config)
            .build();
    }
}
