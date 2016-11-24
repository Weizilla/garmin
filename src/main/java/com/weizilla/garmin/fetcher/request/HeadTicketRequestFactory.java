package com.weizilla.garmin.fetcher.request;

import com.weizilla.garmin.GarminException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(3)
public class HeadTicketRequestFactory extends RequestFactory
{
    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        Pattern ticketPattern = Pattern.compile("ticket=(.*)'");
        Matcher matcher = ticketPattern.matcher(prevResult);
        if (matcher.find())
        {
            String ticket = matcher.group(1);
            BasicNameValuePair ticketPair = new BasicNameValuePair("ticket", ticket);
            String ticketUrl = "?" + URLEncodedUtils.format(Collections.singleton(ticketPair), StandardCharsets.UTF_8);
            String fullTicketUrl = "https://connect.garmin.com/post-auth/login" + ticketUrl;
            return new HttpGet(fullTicketUrl);
        }
        throw new GarminException("Ticket not found in resulting html");
    }

    @Override
    public boolean isExtractResult()
    {
        return false;
    }
}
