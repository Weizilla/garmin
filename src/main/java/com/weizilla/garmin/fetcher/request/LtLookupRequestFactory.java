package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class LtLookupRequestFactory extends RequestFactory
{
    @Override
    public Request create(String prevResult) throws IOException
    {
        HttpGet request = new HttpGet("https://sso.garmin.com/sso/login" + PARAMS_URL);
        return new Request(request, false, true);
    }
}
