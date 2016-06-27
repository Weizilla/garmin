package com.weizilla.garmin.fetcher.step;

import com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Login extends FetchStep
{
    private static final BasicNameValuePair EVENT_ID = new BasicNameValuePair("_eventId", "submit");
    private static final BasicNameValuePair EMBED = new BasicNameValuePair("embed", "true");
    private static final BasicNameValuePair DISPLAY_NAME = new BasicNameValuePair("displayNameRequired", "false");
    private static final List<BasicNameValuePair> PARAMS = Lists.newArrayList(EVENT_ID, EMBED, DISPLAY_NAME);
    private final String username;
    private final String password;

    public Login(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public String execute(String lastResult, CloseableHttpClient httpClient) throws IOException
    {
        List<NameValuePair> data = new ArrayList<>();
        data.addAll(PARAMS);
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("password", password));
        data.add(new BasicNameValuePair("lt", parseLt(lastResult)));

        HttpPost request = new HttpPost("https://sso.garmin.com/sso/login" + PARAMS_URL);
        request.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8));
        return executeRequest(request, httpClient);
    }

    private static String parseLt(String lastResult)
    {
        Document doc = Jsoup.parse(lastResult);
        return doc.select("input[name=lt]").first().attr("value");
    }
}
