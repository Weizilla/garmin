package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetActivitiesStepTest {
    private GetActivitiesStep step;

    @Before
    public void setUp() throws Exception {
        step = new GetActivitiesStep(new UrlBases());
    }

    @Test
    public void returnsHttpRequest() throws Exception {
        HttpUriRequest request = step.create();
        assertThat(request.getURI().toString()).contains(GetActivitiesStep.GET_ACTIVITIES_URL);
    }
}
