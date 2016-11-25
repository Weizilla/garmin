package com.weizilla.garmin.fetcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "garmin")
public class Credentials
{
    /**
     * The username to connect to Garmin with
     */
    private String username;

    /**
     * The password to connect to Garmin with
     */
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
