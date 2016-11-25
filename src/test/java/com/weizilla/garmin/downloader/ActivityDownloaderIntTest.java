package com.weizilla.garmin.downloader;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import com.weizilla.garmin.entity.Activity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ActivityDownloaderIntTest
{
    @Autowired
    private ActivityDownloader downloader;
    @Rule
    public WireMockRule wireMock = new WireMockRule(options().port(8080));

    @Test
    public void downloaderIsInitializedCorrectlyBySpring() throws Exception
    {
        assertThat(downloader).isNotNull();
    }

    @Test
    public void downloadsActivitiesWithUrlCalls() throws Exception
    {
        stubFor(get(urlMatching("/sso/login.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/lt-lookup-response.html"))));
        stubFor(post(urlMatching("/sso/login.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/html; charset=utf-8")
                .withBody(readResource("int-test/login-response.html"))));
        stubFor(get(urlMatching("/post-auth/login.*"))
            .willReturn(aResponse()
                .withHeader("location", "http://localhost:8080/redirect")));
        stubFor(get(urlMatching("/proxy/activity-search-service-1.2.*"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(readResource("int-test/get-activities-response.json"))));
        List<Activity> activities = downloader.download();
        assertThat(activities).isNotEmpty();
    }

    private static String readResource(String filename) throws Exception
    {
        URL url = Resources.getResource(filename);
        return Resources.toString(url, StandardCharsets.UTF_8);
    }
}
