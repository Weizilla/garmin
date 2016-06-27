package com.weizilla.garmin.fetcher.step;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

public class GetActivities extends FetchStep
{
    @Override
    public String execute(String lastResult, CloseableHttpClient httpClient) throws IOException
    {
        HttpGet request = new HttpGet("https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?");
        return executeRequest(request, httpClient);
    }
}
