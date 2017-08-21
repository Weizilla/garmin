package com.weizilla.garmin.fetcher.step;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Step
{
    protected static final String SSO_URL;

    static
    {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("service", "https://connect.garmin.com/post-auth/login"));
        params.add(new BasicNameValuePair("clientId", "GarminConnect"));
        params.add(new BasicNameValuePair("consumeServiceTicket", "false"));
        params.add(new BasicNameValuePair("gauthHost", "https://sso.garmin.com/sso"));
        SSO_URL = "/sso/login?" + encode(params);
    }

    public abstract HttpUriRequest create(String prevResult) throws IOException;

    public abstract boolean isExtractResult();

    public abstract String getStepName();

    protected static String encode(String key, String value)
    {
        return encode(Collections.singletonList(new BasicNameValuePair(key, value)));
    }

    protected static String encode(List<NameValuePair> params)
    {
        return URLEncodedUtils.format(params, StandardCharsets.UTF_8);
    }
}
