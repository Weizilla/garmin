package com.weizilla.garmin.fetcher.step;

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
public class LoginStep extends Step {
    private static final BasicNameValuePair EVENT_ID = new BasicNameValuePair("_eventId", "submit");
    private static final BasicNameValuePair EMBED = new BasicNameValuePair("embed", "true");
    private static final BasicNameValuePair DISPLAY_NAME =
        new BasicNameValuePair("displayNameRequired", "false");
    private static final List<BasicNameValuePair> PARAMS =
        Lists.newArrayList(EVENT_ID, EMBED, DISPLAY_NAME);
    private final UrlBases urlBases;
    private final Credentials credentials;

    @Inject
    public LoginStep(UrlBases urlBases, Credentials credentials) {
        this.urlBases = urlBases;
        this.credentials = credentials;
    }

    public HttpUriRequest create() {
        List<NameValuePair> data = new ArrayList<>(PARAMS);
        data.add(new BasicNameValuePair("username", credentials.getUsername()));
        data.add(new BasicNameValuePair("password", credentials.getPassword()));

        HttpPost request = new HttpPost(urlBases.getLogin() + SSO_URL);
        request.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8));
        return request;
    }

    public String getStepName() {
        return "Login";
    }
}
