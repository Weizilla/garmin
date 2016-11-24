package com.weizilla.garmin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "garmin.url-base")
public class UrlBases
{
    /**
     * The base url for getting activities
     */
    private String getActivities;
    /**
     * The base url for getting the ticket
     */
    private String getTicket;
    /**
     * The base url for logging in with SSO
     */
    private String login;
    /**
     * The base url for looking up the LT value
     */
    private String ltLookup;

    public String getGetActivities()
    {
        return getActivities;
    }

    public void setGetActivities(String getActivities)
    {
        this.getActivities = getActivities;
    }

    public String getGetTicket()
    {
        return getTicket;
    }

    public void setGetTicket(String getTicket)
    {
        this.getTicket = getTicket;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getLtLookup()
    {
        return ltLookup;
    }

    public void setLtLookup(String ltLookup)
    {
        this.ltLookup = ltLookup;
    }
}
