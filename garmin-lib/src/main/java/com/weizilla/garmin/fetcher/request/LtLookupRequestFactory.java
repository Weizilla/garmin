package com.weizilla.garmin.fetcher.request;

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
public class LtLookupRequestFactory extends RequestFactory
{
    private final UrlBases urlBases;

    @Inject
    public LtLookupRequestFactory(UrlBases urlBases)
    {
        this.urlBases = urlBases;
    }

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        return new HttpGet(urlBases.getLtLookup() + SSO_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }

    @Override
    public String getStepName()
    {
        return "LT Lookup";
    }
}