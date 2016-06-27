package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadTicketRequestFactory extends RequestFactory
{
    @Override
    public Request create(String prevResult) throws IOException
    {
        return new Request(createHttpRequest(prevResult), false, false);
    }

    private static HttpRequestBase createHttpRequest(String lastResult) throws IOException
    {
        Pattern ticketPattern = Pattern.compile("ticket=(.*)'");
        Matcher matcher = ticketPattern.matcher(lastResult);
        if (matcher.find())
        {
            String ticket = matcher.group(1);
            BasicNameValuePair ticketPair = new BasicNameValuePair("ticket", ticket);
            String ticketUrl = "?" + URLEncodedUtils.format(Collections.singleton(ticketPair), StandardCharsets.UTF_8);
            String fullTicketUrl = "https://connect.garmin.com/post-auth/login" + ticketUrl;
            return new HttpHead(fullTicketUrl);
        }
        throw new IOException("Ticket not found in resulting html");
    }
}
