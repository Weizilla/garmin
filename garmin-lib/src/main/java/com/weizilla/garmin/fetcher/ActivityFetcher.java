package com.weizilla.garmin.fetcher;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.weizilla.garmin.GarminException;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.NameValuePair;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ActivityFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final List<NameValuePair> LOGIN_PARAMS = new ArrayList<>();
    private static final List<BasicNameValuePair> LOGIN_DATA = new ArrayList<>();

    static {
        LOGIN_PARAMS.add(new BasicNameValuePair("service", "https://connect.garmin.com/modern/"));
        LOGIN_PARAMS.add(new BasicNameValuePair("webhost", "https://connect.garmin.com"));
        LOGIN_PARAMS.add(new BasicNameValuePair("source", "https://sso.garmin.com/sso/login"));
        LOGIN_PARAMS.add(new BasicNameValuePair("redirectAfterAccountLoginUrl", "https://connect.garmin.com/modern/"));
        LOGIN_PARAMS.add(new BasicNameValuePair("redirectAfterAccountCreationUrl", "https://connect.garmin.com/modern/"));
        LOGIN_PARAMS.add(new BasicNameValuePair("gauthHost", "https://sso.garmin.com/sso"));
        LOGIN_PARAMS.add(new BasicNameValuePair("locale", "en_US"));
        LOGIN_PARAMS.add(new BasicNameValuePair("id", "gauth-widget"));
        LOGIN_PARAMS.add(new BasicNameValuePair("cssUrl", "https://static.garmincdn.com/com.garmin.connect/ui/css/gauth-custom-v1.2-min.css"));
        LOGIN_PARAMS.add(new BasicNameValuePair("clientId", "GarminConnect"));
        LOGIN_PARAMS.add(new BasicNameValuePair("rememberMeShown", "true"));
        LOGIN_PARAMS.add(new BasicNameValuePair("rememberMeChecked", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("createAccountShown", "true"));
        LOGIN_PARAMS.add(new BasicNameValuePair("openCreateAccount", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("usernameShown", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("displayNameShown", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("consumeServiceTicket", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("initialFocus", "true"));
        LOGIN_PARAMS.add(new BasicNameValuePair("embedWidget", "false"));
        LOGIN_PARAMS.add(new BasicNameValuePair("generateExtraServiceTicket", "false"));

        LOGIN_DATA.add(new BasicNameValuePair("_eventId", "submit"));
        LOGIN_DATA.add(new BasicNameValuePair("embed", "true"));
        LOGIN_DATA.add(new BasicNameValuePair("displayNameRequired", "false"));
    }

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";

    static final String LOGIN_PATH = "/sso/login";
    static final String FOLLOW_TICKET_PATH = "/modern";
    static final String GET_ACTIVITIES_PATH = "/modern/proxy/activitylist-service/activities/search/activities";

    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;

    private final HttpClientFactory clientFactory;
    private final UrlBases urlBases;
    private final int rateLimit;
    private final LogConfig logConfig;
    private Credentials credentials;

    @Inject
    public ActivityFetcher(HttpClientFactory clientFactory, Credentials credentials, UrlBases urlBases, LogConfig logConfig) {

        this(clientFactory, credentials, urlBases, DEFAULT_RATE_LIMIT_MS, logConfig);
    }

    public ActivityFetcher(HttpClientFactory clientFactory, Credentials credentials, UrlBases urlBases, int rateLimit, LogConfig logConfig) {

        this.clientFactory = clientFactory;
        this.credentials = credentials;
        this.urlBases = urlBases;
        this.rateLimit = rateLimit;
        this.logConfig = logConfig;
    }

    public String fetch() throws Exception {
        try (CloseableHttpClient client = clientFactory.build()) {
            executeGetLoginLookup(client);
            String ticket = executeSubmitLogin(client);
            executeFollowTicket(ticket, client);
            return executeGetActivities(client);
        }
    }

    private void executeGetLoginLookup(CloseableHttpClient httpClient) throws Exception {
        URI uri = new URIBuilder(urlBases.getLtLookup())
            .setPath(LOGIN_PATH)
            .addParameters(LOGIN_PARAMS)
            .build();
        HttpUriRequest request = new HttpGet(uri);
        logRequest("Get login", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Get login", result);
        }
    }

    private String executeSubmitLogin(CloseableHttpClient httpClient) throws Exception {
        List<NameValuePair> loginData = new ArrayList<>(LOGIN_DATA);
        loginData.add(new BasicNameValuePair("username", credentials.getUsername()));
        loginData.add(new BasicNameValuePair("password", credentials.getPassword()));

        URI uri = new URIBuilder(urlBases.getLogin())
            .setPath(LOGIN_PATH)
            .addParameters(LOGIN_PARAMS)
            .build();
        HttpPost request = new HttpPost(uri);
        request.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
        request.addHeader(HttpHeaders.REFRESH, "0");
        request.setEntity(new UrlEncodedFormEntity(loginData, StandardCharsets.UTF_8));

        logRequest("Submit login", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("submit login", result);
            return parseForTicket(result);
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

    private void executeFollowTicket(String ticket, CloseableHttpClient httpClient) throws Exception {
        URI uri = new URIBuilder(urlBases.getFollowTicket())
            .setPath(FOLLOW_TICKET_PATH)
            .addParameter("ticket", ticket)
            .build();
        HttpUriRequest request = new HttpGet(uri);
        logRequest("Follow ticket", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Follow ticket", result);
        }
    }

    private String executeGetActivities(CloseableHttpClient httpClient) throws Exception {
        URI uri = new URIBuilder(urlBases.getGetActivities())
            .setPath(GET_ACTIVITIES_PATH)
            .build();
        HttpUriRequest request = new HttpGet(uri);
        logRequest("Get activities", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Get activities", result);
            return result;
        }
    }

    private void logRequest(String name, HttpUriRequest request) {
        String url = logConfig.isUrl() ? request.getMethod() + " " + request.getURI() : "";
        logger.info("Executing step [{}] {}", name, url);
    }

    private void logResult(String name, String result) {
        if (logConfig.isResult()) {
            logger.info("Result for step [{}]\n{}", name, result);
        }
    }
}
