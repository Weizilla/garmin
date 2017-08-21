package com.weizilla.garmin.fetcher.request;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Order = 4
 */
@Singleton
public class GetActivitiesRequestFactory extends RequestFactory
{
    protected static final String GET_ACTIVITIES_URL = "/proxy/activity-search-service-1.2/json/activities";
    private final UrlBases urlBases;

    @Inject
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
