package com.weizilla.garmin.downloader;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.configuration.UrlBases;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import com.weizilla.garmin.fetcher.HttpClientFactory;
import com.weizilla.garmin.fetcher.step.FollowTicketStep;
import com.weizilla.garmin.fetcher.step.GetActivitiesStep;
import com.weizilla.garmin.fetcher.step.LoginStep;
import com.weizilla.garmin.fetcher.step.LtLookupStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.weizilla.test.TestUtils.readResource;
import static org.assertj.core.api.Assertions.assertThat;

public class ActivityDownloaderIntTest {
    private static final int PORT = 8080;
    private ActivityDownloader downloader;
    @Rule
    public WireMockRule wireMock = new WireMockRule(options().port(PORT));

    @Before
    public void setUp() throws Exception {
        UrlBases bases = new UrlBases();
        bases.setFollowTicket("http://localhost:8080");
        bases.setGetActivities("http://localhost:8080");
        bases.setLogin("http://localhost:8080");
        bases.setLtLookup("http://localhost:8080");

        LogConfig logConfig = new LogConfig();
        logConfig.setResult(false);
        logConfig.setUrl(true);

        Credentials credentials = new Credentials();
        credentials.setUsername("USERNAME");
        credentials.setPassword("PASSWORD");

        HttpClientFactory httpClientFactory = new HttpClientFactory();
        LtLookupStep ltLookupStep = new LtLookupStep(bases);
        LoginStep loginStep = new LoginStep(bases, credentials);
        FollowTicketStep followTicketStep =
            new FollowTicketStep(bases);
        GetActivitiesStep getActivitiesStep =
            new GetActivitiesStep(bases);
        ActivityFetcher fetcher = new ActivityFetcher(httpClientFactory, ltLookupStep,
            loginStep, getActivitiesStep, followTicketStep, logConfig);
        downloader = new ActivityDownloader(new ActivityParser(), fetcher);
    }

    @Test
    public void downloadsActivitiesWithUrlCalls() throws Exception {
        stubFor(get(urlMatching("/sso/login.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/lt-lookup-response.html"))));
        stubFor(post(urlMatching("/sso/login.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/login-response.html"))));
        stubFor(get(urlMatching(FollowTicketStep.POST_AUTH_URL + ".*"))
            .willReturn(aResponse()
                .withHeader("location", "http://localhost:" + PORT + "/redirect")));
        stubFor(get(urlMatching(GetActivitiesStep.GET_ACTIVITIES_URL + ".*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(readResource("int-test/get-activities-response.json"))));
        List<Activity> activities = downloader.download();
        assertThat(activities).isNotEmpty();
    }
}
