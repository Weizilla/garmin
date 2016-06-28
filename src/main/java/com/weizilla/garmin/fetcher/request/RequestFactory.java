package com.weizilla.garmin.fetcher.request;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class RequestFactory
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

    public abstract HttpUriRequest create(String prevResult) throws IOException;

    public abstract boolean isExtractResult();
}
