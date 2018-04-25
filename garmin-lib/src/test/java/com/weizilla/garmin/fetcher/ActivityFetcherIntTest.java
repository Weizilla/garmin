package com.weizilla.garmin.fetcher;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.configuration.UrlBases;
import com.weizilla.garmin.downloader.ActivityDownloader;
import com.weizilla.garmin.downloader.ActivityParser;
import com.weizilla.garmin.entity.Activity;
import com.weizilla.garmin.fetcher.ActivityFetcher;
import com.weizilla.garmin.fetcher.HttpClientFactory;
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
import static com.weizilla.garmin.fetcher.ActivityFetcher.GET_ACTIVITIES_PATH;
import static com.weizilla.garmin.fetcher.ActivityFetcher.FOLLOW_TICKET_PATH;
import static com.weizilla.garmin.fetcher.ActivityFetcher.LOGIN_PATH;
import static com.weizilla.test.TestUtils.readResource;
import static org.assertj.core.api.Assertions.assertThat;

public class ActivityFetcherIntTest {
    private static final int PORT = 8080;
    @Rule
    public WireMockRule wireMock = new WireMockRule(options().port(PORT));
    private ActivityFetcher fetcher;

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
        fetcher = new ActivityFetcher(httpClientFactory,credentials, bases, 0, logConfig);

        stubFor(get(urlMatching(".*" + LOGIN_PATH + ".*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/get-login-response.html"))));
        stubFor(post(urlMatching(".*" + LOGIN_PATH + ".*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/submit-login-response.html"))));
        stubFor(get(urlMatching(".*" + FOLLOW_TICKET_PATH + "\\?ticket=.*"))
            .willReturn(aResponse()
                .withHeader("location", "http://localhost:" + PORT + "/redirect")));
        stubFor(get(urlMatching(".*" + GET_ACTIVITIES_PATH))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(readResource("int-test/get-activities-response.json"))));
    }

    @Test
    public void returnsNonEmptyJson() throws Exception {
        String json = fetcher.fetch();
        assertThat(json).isNotBlank();
    }
}
