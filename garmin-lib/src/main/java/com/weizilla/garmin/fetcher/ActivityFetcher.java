package com.weizilla.garmin.fetcher;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.weizilla.garmin.GarminException;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.fetcher.step.FollowTicketStep;
import com.weizilla.garmin.fetcher.step.GetActivitiesStep;
import com.weizilla.garmin.fetcher.step.LoginStep;
import com.weizilla.garmin.fetcher.step.LtLookupStep;
import com.weizilla.garmin.fetcher.step.Step;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ActivityFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final int rateLimit;
    private final LogConfig logConfig;
    private final LtLookupStep ltLookupStep;
    private final LoginStep loginStep;
    private final FollowTicketStep followTicketStep;
    private final GetActivitiesStep getActivitiesStep;

    @Inject
    public ActivityFetcher(HttpClientFactory clientFactory,
        LtLookupStep ltLookupStep,
        LoginStep loginStep,
        GetActivitiesStep getActivitiesStep,
        FollowTicketStep followTicketStep,
        LogConfig logConfig) {

        this(clientFactory, ltLookupStep, loginStep,
            getActivitiesStep, followTicketStep, DEFAULT_RATE_LIMIT_MS, logConfig);
    }

    public ActivityFetcher(HttpClientFactory clientFactory, LtLookupStep ltLookupStep,
        LoginStep loginStep, GetActivitiesStep getActivitiesStep,
        FollowTicketStep followTicketStep, int rateLimit, LogConfig logConfig) {

        this.clientFactory = clientFactory;
        this.ltLookupStep = ltLookupStep;
        this.loginStep = loginStep;
        this.followTicketStep = followTicketStep;
        this.getActivitiesStep = getActivitiesStep;
        this.rateLimit = rateLimit;
        this.logConfig = logConfig;
    }

    public String fetch() throws Exception {
        try (CloseableHttpClient client = clientFactory.build()) {
            executeLtLookup(client);
            String ticket = executeLogin(client);
            executeFollowTicket(ticket, client);
            return executeGetActivities(client);
        }
    }

    private void executeLtLookup(CloseableHttpClient httpClient) throws Exception {
        HttpUriRequest request = ltLookupStep.create();
        logRequest("Lt lookup", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Lt lookup", result);
        }
    }

    private String executeLogin(CloseableHttpClient httpClient) throws Exception {
        HttpUriRequest request = loginStep.create();
        logRequest("Login", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Login", result);
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
        HttpUriRequest request = followTicketStep.create(ticket);
        logRequest("Lt lookup", request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            logResult("Lt lookup", result);
        }
    }

    private String executeGetActivities(CloseableHttpClient httpClient) throws Exception {
        HttpUriRequest request = getActivitiesStep.create();
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

    private void logResult(String name, String result) throws IOException {
        if (logConfig.isResult()) {
            logger.info("Result for {}:\n{}", name, result);
        }
    }

    protected int getRateLimit() {
        return rateLimit;
    }
}
