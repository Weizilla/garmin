package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

public class LtLookupRequestFactory extends RequestFactory
{
    private static final String LOGIN_URL = "https://sso.garmin.com/sso/login" + PARAMS_URL;

    @Override
    public HttpRequestBase create(String prevResult) throws IOException
    {
        return new HttpGet(LOGIN_URL);
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }
}
