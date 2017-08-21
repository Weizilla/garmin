package com.weizilla.garmin.fetcher.request;

import com.weizilla.garmin.GarminException;
import com.weizilla.garmin.configuration.UrlBases;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Order = 3
 */
public class FollowTicketRequestFactory extends RequestFactory
{
    private static final String POST_AUTH_URL = "/post-auth/login?";
    private final UrlBases urlBases;

    public FollowTicketRequestFactory(UrlBases urlBases)
    {
        this.urlBases = urlBases;
    }

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        Pattern ticketPattern = Pattern.compile("ticket=(.*)'");
        Matcher matcher = ticketPattern.matcher(prevResult);
        if (matcher.find())
        {
            String ticket = matcher.group(1);
            return new HttpGet(urlBases.getFollowTicket() + POST_AUTH_URL + encode("ticket", ticket));
        }
        throw new GarminException("Ticket not found in resulting html");
    }

    @Override
    public boolean isExtractResult()
    {
        return false;
    }

    @Override
    public String getStepName()
    {
        return "Follow Ticket";
    }
}
