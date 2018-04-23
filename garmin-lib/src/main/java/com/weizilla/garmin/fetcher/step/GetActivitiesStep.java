package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Order = 4
 */
@Singleton
public class GetActivitiesStep extends Step {
//    public static final String GET_ACTIVITIES_URL = "/modern/proxy/activitylist-service/activities/search/activities/";
    public static final String GET_ACTIVITIES_URL =
        "/proxy/activity-search-service-1.2/json/activities";
    private final UrlBases urlBases;

    @Inject
    public GetActivitiesStep(UrlBases urlBases) {
        this.urlBases = urlBases;
    }

    public HttpUriRequest create() {
        return new HttpGet(urlBases.getGetActivities() + GET_ACTIVITIES_URL);
    }
}
