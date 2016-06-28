package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class LtLookupRequestFactory extends RequestFactory
{
    protected static final String LOGIN_URL = "https://sso.garmin.com/sso/login" + PARAMS_URL;

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        return new HttpGet(LOGIN_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }
}
