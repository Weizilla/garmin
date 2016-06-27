package com.weizilla.garmin.fetcher.step;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadTicket extends FetchStep
{
    @Override
    public String execute(String lastResult, CloseableHttpClient httpClient) throws IOException
    {
        Pattern ticketPattern = Pattern.compile("ticket=(.*)'");
        Matcher matcher = ticketPattern.matcher(lastResult);
        if (matcher.find())
        {
            String ticket = matcher.group(1);
            BasicNameValuePair ticketPair = new BasicNameValuePair("ticket", ticket);
            String ticketUrl = "?" + URLEncodedUtils.format(Collections.singleton(ticketPair), StandardCharsets.UTF_8);
            String fullTicketUrl = "https://connect.garmin.com/post-auth/login" + ticketUrl;

            HttpHead request = new HttpHead(fullTicketUrl);
            try (CloseableHttpResponse response = httpClient.execute(request))
            {
                //TODO check for code
                return null;
            }
        }
        throw new IOException("Ticket not found in resulting html");
    }
}
