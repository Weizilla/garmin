package com.weizilla.garmin.fetcher;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivitiesFetcher
{
    private static final Logger logger = LoggerFactory.getLogger(ActivitiesFetcher.class);

    private final String username;
    private final String password;

    public ActivitiesFetcher(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String fetch() throws Exception
    {
        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpClientBuilder clientBuilder = HttpClients.custom().setDefaultCookieStore(cookieStore);

        try (CloseableHttpClient client = clientBuilder.build())
        {
            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("username", username));
            data.add(new BasicNameValuePair("password", password));
            data.add(new BasicNameValuePair("_eventId", "submit"));
            data.add(new BasicNameValuePair("embed", "true"));
            data.add(new BasicNameValuePair("displayNameRequired", "false"));

            List<NameValuePair> params = new ArrayList<>();
            params.add(
                new BasicNameValuePair("service", "https://connect.garmin.com/post-auth/login"));
            params.add(new BasicNameValuePair("clientId", "GarminConnect"));
            params.add(new BasicNameValuePair("consumeServiceTicket", "false"));

            String paramsUrl = "?" + URLEncodedUtils.format(params, StandardCharsets.UTF_8);
            HttpGet preResp = new HttpGet("https://sso.garmin.com/sso/login" + paramsUrl);
            logger.info("preResp uri: {}", preResp);

            try (CloseableHttpResponse response = client.execute(preResp))
            {
                logger.info("Pre resp response: {}", response.getStatusLine());
                Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
                Element lt = doc.select("input[name=lt]").first();
                String ltValue = lt.attr("value");
                data.add(new BasicNameValuePair("lt", ltValue));
                logger.info("Lt element: {}", ltValue);
            }

            HttpPost sso = new HttpPost("https://sso.garmin.com/sso/login" + paramsUrl);
            sso.setEntity(new UrlEncodedFormEntity(data));
            logger.info("sso uri: {}", sso);
            try (CloseableHttpResponse response = client.execute(sso))
            {
                logger.info("sso response: {}", response.getStatusLine());
                String responseHtml = EntityUtils.toString(response.getEntity());
                Pattern ticketPattern = Pattern.compile("ticket=(.*)'");
                Matcher matcher = ticketPattern.matcher(responseHtml);
                if (matcher.find())
                {
                    String ticket = matcher.group(1);
                    logger.info("sso ticket: {}", ticket);
                    String ticketUrl = "?" + URLEncodedUtils.format(
                        Collections.singleton(new BasicNameValuePair("ticket", ticket)),
                        StandardCharsets.UTF_8);
                    String fullTicketUrl = "https://connect.garmin.com/post-auth/login" + ticketUrl;
                    logger.info("sso ticket url: {}", fullTicketUrl);
                    HttpHead auth = new HttpHead(fullTicketUrl);

                    Thread.sleep(1000);
                    try (CloseableHttpResponse response1 = client.execute(auth))
                    {
                        logger.info("gc redeem resp 1 response: {}", response1.getStatusLine());
                        HttpGet activities = new HttpGet(
                            "https://connect.garmin.com/proxy/activity-search-service-1.2/json/activities?");
                        Thread.sleep(1000);
                        try (CloseableHttpResponse activitiesResponse = client.execute(activities))
                        {
                            logger.info("Activities response: {}",
                                activitiesResponse.getStatusLine());
                            String json = EntityUtils.toString(activitiesResponse.getEntity());
                            logger.info("Activities entity: {}", json);
                            return json;
                        }
                    }
                }
                else
                {
                    logger.error("sso failure. ticket not found");
                }
            }
        }
        return null;
    }
}
