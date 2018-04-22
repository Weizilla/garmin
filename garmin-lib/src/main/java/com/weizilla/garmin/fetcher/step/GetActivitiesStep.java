package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Order = 4
 */
@Singleton
public class GetActivitiesStep extends Step {
    public static final String GET_ACTIVITIES_URL =
        "/proxy/activitylist-service/activities/search/activities/";
    private final UrlBases urlBases;

    @Inject
    public GetActivitiesStep(UrlBases urlBases) {
        this.urlBases = urlBases;
    }

    @Override
    public HttpUriRequest create(String prevResult) {
        return new HttpGet(urlBases.getGetActivities() + GET_ACTIVITIES_URL);
    }

    @Override
    public boolean isExtractResult() {
        return true;
    }

    @Override
    public String getStepName() {
        return "Get Activities";
    }
}
