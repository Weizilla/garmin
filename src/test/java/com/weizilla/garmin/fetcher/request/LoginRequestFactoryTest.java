package com.weizilla.garmin.fetcher.request;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.weizilla.garmin.UrlBases;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginRequestFactoryTest
{
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private LoginRequestFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = new LoginRequestFactory(new UrlBases(), USERNAME, PASSWORD);
    }

    @Test
    public void createsHttpPostRequestWithUsernameAndPassword() throws Exception
    {
        HttpUriRequest request = factory.create("<input name='lt' value='a1b2c3'/>\"");
        HttpPost post = (HttpPost) request;
        HttpEntity entity = post.getEntity();
        String entities = CharStreams.toString(new InputStreamReader(entity.getContent(), Charsets.UTF_8));
        assertThat(entities).contains(USERNAME);
        assertThat(entities).contains(PASSWORD);
    }

    @Test
    public void parsesHtmlForLtValue() throws Exception
    {
        String lt = "a1b2c3";
        String input = "<input name='lt' value='" + lt + "'/>";
        HttpUriRequest request = factory.create(input);
        HttpPost post = (HttpPost) request;
        HttpEntity entity = post.getEntity();
        String entities = CharStreams.toString(new InputStreamReader(entity.getContent(), Charsets.UTF_8));
        assertThat(entities).contains(lt);
    }

    @Test
    public void doesNotExtractResult() throws Exception
    {
        assertThat(factory.isExtractResult()).isTrue();
    }
}