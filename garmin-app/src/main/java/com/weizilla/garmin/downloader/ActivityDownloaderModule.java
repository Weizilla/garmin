package com.weizilla.garmin.downloader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.weizilla.garmin.configuration.Credentials;
import com.weizilla.garmin.configuration.LogConfig;
import com.weizilla.garmin.configuration.UrlBases;

import javax.inject.Singleton;

public class ActivityDownloaderModule extends AbstractModule {
    private final UrlBases bases;
    private final LogConfig logConfig;
    private final Credentials credentials;

    public ActivityDownloaderModule(String username, String password) {
        bases = new UrlBases();
        bases.setFollowTicket("https://connect.garmin.com");
        bases.setGetActivities("https://connect.garmin.com");
        bases.setLogin("https://sso.garmin.com");
        bases.setLtLookup("https://sso.garmin.com");

        logConfig = new LogConfig();
        logConfig.setResult(false);
        logConfig.setUrl(true);

        credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
    }

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    public UrlBases getBases() {
        return bases;
    }

    @Singleton
    @Provides
    public LogConfig getLogConfig() {
        return logConfig;
    }

    @Singleton
    @Provides
    public Credentials getCredentials() {
        return credentials;
    }
}
