package com.weizilla.garmin.fetcher.request;

import com.weizilla.garmin.fetcher.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class GetActivitiesRequestFactory extends RequestFactory
{
    protected static final String GET_ACTIVITIES_URL = "/proxy/activity-search-service-1.2/json/activities";
    private final UrlBases urlBases;

    @Autowired
    public GetActivitiesRequestFactory(UrlBases urlBases)
    {
        this.urlBases = urlBases;
    }

    @Override
    public HttpUriRequest create(String prevResult)
    {
        return new HttpGet(urlBases.getGetActivities() + GET_ACTIVITIES_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }

    @Override
    public String getStepName()
    {
        return "Get Activities";
    }
}
