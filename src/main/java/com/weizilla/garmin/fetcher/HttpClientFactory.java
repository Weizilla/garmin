package com.weizilla.garmin.fetcher;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientFactory
{
    public CloseableHttpClient build()
    {
        return HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
    }
}