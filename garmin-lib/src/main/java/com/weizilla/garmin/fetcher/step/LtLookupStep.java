package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * Order = 1
 */
@Singleton
public class LtLookupStep extends Step {
    private final UrlBases urlBases;

    @Inject
    public LtLookupStep(UrlBases urlBases) {
        this.urlBases = urlBases;
    }

    public HttpUriRequest create() {
        return new HttpGet(urlBases.getLtLookup() + SSO_URL);
    }

    public String getStepName() {
        return "LT Lookup";
    }
}
