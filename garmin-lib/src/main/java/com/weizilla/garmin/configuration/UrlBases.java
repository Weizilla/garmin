package com.weizilla.garmin.configuration;

public class UrlBases
{
    /**
     * The base url for getting activities
     */
    private String getActivities;
    /**
     * The base url for following ticket redirects
     */
    private String followTicket;
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

    public String getFollowTicket()
    {
        return followTicket;
    }

    public void setFollowTicket(String followTicket)
    {
        this.followTicket = followTicket;
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
