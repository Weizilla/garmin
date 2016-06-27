package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class GetActivitiesRequestFactory extends RequestFactory
{
    private static final String GET_ACTIVITES_URL =
        "https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?";

    @Override
    public HttpRequestBase create(String prevResult)
    {
        return new HttpGet(GET_ACTIVITES_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }
}
