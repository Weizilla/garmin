package com.weizilla.garmin.fetcher.step;

import com.weizilla.garmin.GarminException;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Order = 3
 */
@Singleton
public class FollowTicketStep extends Step {
    public static final String POST_AUTH_URL = "/post-auth/login?";
    private final UrlBases urlBases;

    @Inject
    public FollowTicketStep(UrlBases urlBases) {
        this.urlBases = urlBases;
    }

    public HttpUriRequest create(String ticket) throws IOException {
        return new HttpGet(urlBases.getFollowTicket() + POST_AUTH_URL + encode("ticket", ticket));
    }
}
