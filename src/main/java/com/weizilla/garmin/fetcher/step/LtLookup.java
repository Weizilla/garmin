package com.weizilla.garmin.fetcher.step;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

public class LtLookup extends FetchStep
{
    @Override
    public String execute(String lastResult, CloseableHttpClient httpClient) throws IOException
    {
        HttpGet request = new HttpGet("https://sso.garmin.com/sso/login" + PARAMS_URL);
        return executeRequest(request, httpClient);
    }
}
