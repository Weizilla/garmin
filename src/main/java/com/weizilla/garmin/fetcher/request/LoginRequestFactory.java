package com.weizilla.garmin.fetcher.request;

import com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final String username;
    private final String password;

    @Autowired
    public LoginRequestFactory(@Value("${garminUsername}") String username, @Value("${garminPassword}") String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        List<NameValuePair> data = new ArrayList<>();
        data.addAll(PARAMS);
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("password", password));
        data.add(new BasicNameValuePair("lt", parseLt(prevResult)));

        HttpPost request = new HttpPost("https://sso.garmin.com/sso/login" + PARAMS_URL);
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
}
