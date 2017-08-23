package com.weizilla.garmin.fetcher;

import com.google.common.collect.Lists;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.fetcher.step.FollowTicketStep;
import com.weizilla.garmin.fetcher.step.GetActivitiesStep;
import com.weizilla.garmin.fetcher.step.LoginStep;
import com.weizilla.garmin.fetcher.step.LtLookupStep;
import com.weizilla.garmin.fetcher.step.Step;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ActivityFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ActivityFetcher.class);
    protected static final int DEFAULT_RATE_LIMIT_MS = 1000;
    private final HttpClientFactory clientFactory;
    private final List<Step> steps;
    private final int rateLimit;
    private final LogConfig logConfig;

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
        steps = Lists.newArrayList(ltLookupStep, loginStep, followTicketStep, getActivitiesStep);
        this.rateLimit = rateLimit;
        this.logConfig = logConfig;
    }

    public String fetch() throws Exception {
        String lastResult = null;
        try (CloseableHttpClient client = clientFactory.build()) {
            //TODO refactor out this loop as steps are always the same and in the?
            // same order
            for (Step step : steps) {
                lastResult = execute(step, lastResult, client);
            }
        }
        return lastResult;
    }

    private String execute(Step step, String lastResult, CloseableHttpClient httpClient) throws Exception {
        HttpUriRequest request = step.create(lastResult);
        logRequest(step, request);
        Thread.sleep(rateLimit);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = step.isExtractResult() && response != null
                ? EntityUtils .toString(response.getEntity())
                : null;
            logResult(step, result);
            return result;
        }
    }

    private void logRequest(Step step, HttpUriRequest request) {
        String url = logConfig.isUrl() ? request.getMethod() + " " + request.getURI() : "";
        logger.info("Executing step [{}] {}", step.getStepName(), url);
    }

    private void logResult(Step step, String result) {
        if (logConfig.isResult()) {
            logger.info("Result for {}:\n{}", step.getStepName(), result);
        }
    }

    protected int getRateLimit() {
        return rateLimit;
    }
}
