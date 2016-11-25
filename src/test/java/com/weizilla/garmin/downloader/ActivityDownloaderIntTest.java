package com.weizilla.garmin.downloader;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
