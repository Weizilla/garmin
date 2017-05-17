package com.weizilla.garmin.configuration;

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
