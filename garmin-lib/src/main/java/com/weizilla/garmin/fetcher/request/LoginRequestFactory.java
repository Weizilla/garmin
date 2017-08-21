package com.weizilla.garmin.fetcher.request;

import com.google.common.collect.Lists;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Order = 2
 */
@Singleton
public class LoginRequestFactory extends RequestFactory
{
    private static final BasicNameValuePair EVENT_ID = new BasicNameValuePair("_eventId", "submit");
    private static final BasicNameValuePair EMBED = new BasicNameValuePair("embed", "true");
    private static final BasicNameValuePair DISPLAY_NAME = new BasicNameValuePair("displayNameRequired", "false");
    private static final List<BasicNameValuePair> PARAMS = Lists.newArrayList(EVENT_ID, EMBED, DISPLAY_NAME);
    private final UrlBases urlBases;
    private final Credentials credentials;

    @Inject
    public LoginRequestFactory(UrlBases urlBases, Credentials credentials)
    {
        this.urlBases = urlBases;
        this.credentials = credentials;
    }

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        List<NameValuePair> data = new ArrayList<>();
        data.addAll(PARAMS);
        data.add(new BasicNameValuePair("username", credentials.getUsername()));
        data.add(new BasicNameValuePair("password", credentials.getPassword()));

        HttpPost request = new HttpPost(urlBases.getLogin() + SSO_URL);
        request.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8));
        return request;
    }

    @Override
    public boolean isExtractResult()
    {
        return true;
    }

    @Override
    public String getStepName()
    {
        return "Login";
    }
}
