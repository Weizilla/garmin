package com.weizilla.garmin.fetcher.step;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginStepTest
{
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private LoginStep step;

    @Before
    public void setUp() throws Exception
    {
        Credentials credentials = new Credentials();
        credentials.setUsername(USERNAME);
        credentials.setPassword(PASSWORD);
        step = new LoginStep(new UrlBases(), credentials);
    }

    @Test
    public void createsHttpPostRequestWithUsernameAndPassword() throws Exception
    {
        HttpUriRequest request = step.create("<input name='lt' value='a1b2c3'/>\"");
        HttpPost post = (HttpPost) request;
        HttpEntity entity = post.getEntity();
        String entities = CharStreams.toString(new InputStreamReader(entity.getContent(), Charsets.UTF_8));
        assertThat(entities).contains(USERNAME);
        assertThat(entities).contains(PASSWORD);
    }

    @Test
    public void doesNotExtractResult() throws Exception
    {
        assertThat(step.isExtractResult()).isTrue();
    }
}