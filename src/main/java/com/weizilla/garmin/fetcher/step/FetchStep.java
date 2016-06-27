package com.weizilla.garmin.fetcher.step;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class FetchStep
{
    protected static final String PARAMS_URL;

    static
    {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("service", "https://connect.garmin.com/post-auth/login"));
        params.add(new BasicNameValuePair("clientId", "GarminConnect"));
        params.add(new BasicNameValuePair("consumeServiceTicket", "false"));
        PARAMS_URL = "?" + URLEncodedUtils.format(params, StandardCharsets.UTF_8);
    }

    public abstract String execute(String lastResult, CloseableHttpClient httpClient) throws IOException;

    protected static String executeRequest(HttpRequestBase request, CloseableHttpClient httpClient) throws IOException
    {
        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            return EntityUtils.toString(response.getEntity());
        }
    }
}