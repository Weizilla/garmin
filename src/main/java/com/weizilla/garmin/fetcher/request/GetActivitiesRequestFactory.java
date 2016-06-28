package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class GetActivitiesRequestFactory extends RequestFactory
{
    protected static final String GET_ACTIVITIES_URL =
        "https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?";

    @Override
    public HttpUriRequest create(String prevResult)
    {
        return new HttpGet(GET_ACTIVITIES_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }
}
