package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;

public class GetActivitiesRequestFactory extends RequestFactory
{
    @Override
    public Request create(String prevResult)
    {
        HttpGet request = new HttpGet("https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?");
        return new Request(request, true, true);
    }
}
