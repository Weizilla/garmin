package com.weizilla.garmin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.weizilla.garmin.fetcher.HttpClientFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple single class for testing all of the steps to getting Garmin activities
 */
public class GarminSpike {
    private static final Logger logger = LoggerFactory.getLogger(GarminSpike.class);

    private static List<NameValuePair> getParams() {
        List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("service", "https://connect.garmin.com/modern/"));
            params.add(new BasicNameValuePair("webhost", "https://connect.garmin.com"));
            params.add(new BasicNameValuePair("source", "https://sso.garmin.com/sso/login"));
            params.add(new BasicNameValuePair("redirectAfterAccountLoginUrl", "https://connect.garmin.com/modern/"));
            params.add(new BasicNameValuePair("redirectAfterAccountCreationUrl", "https://connect.garmin.com/modern/"));
            params.add(new BasicNameValuePair("gauthHost", "https://sso.garmin.com/sso"));
            params.add(new BasicNameValuePair("locale", "en_US"));
            params.add(new BasicNameValuePair("id", "gauth-widget"));
            params.add(new BasicNameValuePair("cssUrl", "https://static.garmincdn.com/com.garmin.connect/ui/css/gauth-custom-v1.2-min.css"));
            params.add(new BasicNameValuePair("clientId", "GarminConnect"));
            params.add(new BasicNameValuePair("rememberMeShown", "true"));
            params.add(new BasicNameValuePair("rememberMeChecked", "false"));
            params.add(new BasicNameValuePair("createAccountShown", "true"));
            params.add(new BasicNameValuePair("openCreateAccount", "false"));
            params.add(new BasicNameValuePair("usernameShown", "false"));
            params.add(new BasicNameValuePair("displayNameShown", "false"));
            params.add(new BasicNameValuePair("consumeServiceTicket", "false"));
            params.add(new BasicNameValuePair("initialFocus", "true"));
            params.add(new BasicNameValuePair("embedWidget", "false"));
            params.add(new BasicNameValuePair("generateExtraServiceTicket", "false"));
            return params;
    }

    public static void main(String[] args) throws Exception {
        String username = args[0];
        String password = args[1];
        try (CloseableHttpClient client = new HttpClientFactory().build())
        {
            logger.info("Getting login");
            URI getLoginUri = new URIBuilder("https://sso.garmin.com/sso/login")
                .addParameters(getParams())
                .build();
            HttpGet getLogin = new HttpGet(getLoginUri);
            execute(client, getLogin);

            logger.info("Logging in");

            List<NameValuePair> loginCreds = new ArrayList<>();
            loginCreds.add(new BasicNameValuePair("displayNameRequired", "false"));
            loginCreds.add(new BasicNameValuePair("_eventId", "submit"));
            loginCreds.add(new BasicNameValuePair("embed", "true"));
            loginCreds.add(new BasicNameValuePair("username", username));
            loginCreds.add(new BasicNameValuePair("password", password));

            logger.info("Getting ticket");
            HttpPost request = new HttpPost(getLoginUri);
            request.addHeader("User-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2");
            request.addHeader("refresh", "0");
            request.setEntity(new UrlEncodedFormEntity(loginCreds, StandardCharsets.UTF_8));
            String postLogin = execute(client, request);

            String ticket = parseForTicket(postLogin);

            logger.info("Following ticket");
            HttpGet followTicket = new HttpGet("https://connect.garmin.com/modern/?ticket=" + ticket);
            execute(client, followTicket);

            logger.info("Getting activites");
            HttpGet activies = new HttpGet("https://connect.garmin.com/modern/proxy/activitylist-service/activities/search/activities?start=0&limit=100");
            execute(client, activies);
        }
    }


    private static String parseForTicket(String result) {
        Pattern ticketPattern = Pattern.compile("ticket=(.*)['\"]");
        Matcher matcher = ticketPattern.matcher(result);
        if (matcher.find()) {
            String ticket = matcher.group(1);
            if (! Strings.isNullOrEmpty(ticket)) {
                return ticket;
            }
        }
        throw new GarminException("Ticket not found in resulting html");
    }

    private static String execute(CloseableHttpClient client, HttpUriRequest request) throws Exception {
        try (CloseableHttpResponse response = client.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logger.info(result);
            return result;
        }
    }
}
