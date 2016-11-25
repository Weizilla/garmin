package com.weizilla.garmin.fetcher.request;

import com.google.common.collect.Lists;
import com.weizilla.garmin.fetcher.Credentials;
import com.weizilla.garmin.fetcher.UrlBases;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class LoginRequestFactory extends RequestFactory
{
    private static final BasicNameValuePair EVENT_ID = new BasicNameValuePair("_eventId", "submit");
    private static final BasicNameValuePair EMBED = new BasicNameValuePair("embed", "true");
    private static final BasicNameValuePair DISPLAY_NAME = new BasicNameValuePair("displayNameRequired", "false");
    private static final List<BasicNameValuePair> PARAMS = Lists.newArrayList(EVENT_ID, EMBED, DISPLAY_NAME);
    private final UrlBases urlBases;
    private final Credentials credentials;

    @Autowired
    public LoginRequestFactory( UrlBases urlBases, Credentials credentials)
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
        data.add(new BasicNameValuePair("lt", parseLt(prevResult)));

        HttpPost request = new HttpPost(urlBases.getLogin() + SSO_URL);
        request.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8));
        return request;
    }

    private static String parseLt(String lastResult)
    {
        Document doc = Jsoup.parse(lastResult);
        return doc.select("input[name=lt]").first().attr("value");
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
