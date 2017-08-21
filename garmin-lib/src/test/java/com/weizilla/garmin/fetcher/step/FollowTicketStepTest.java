package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.GarminException;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowTicketStepTest
{
    private FollowTicketStep step;

    @Before
    public void setUp() throws Exception
    {
        step = new FollowTicketStep(new UrlBases());
    }

    @Test
    public void parsesInputForTicketUrl() throws Exception
    {
        String input = "'https://connect.garmin.com/post-auth/login?ticket=1234ABCD';";
        HttpUriRequest request = step.create(input);
        assertThat(request.getURI().toString()).contains("ticket=1234ABCD");
    }

    @Test(expected = GarminException.class)
    public void throwsExceptionIfNoTicketFound() throws Exception
    {
        step.create("RANDOM INPUT");
    }

    @Test
    public void doesNotExtractResult() throws Exception
    {
        assertThat(step.isExtractResult()).isFalse();
    }
}