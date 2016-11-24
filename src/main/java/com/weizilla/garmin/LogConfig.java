package com.weizilla.garmin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "garmin.log")
public class LogConfig
{
    /**
     * Log the result
     */
    private boolean result;
    /**
     * Log the request URL
     */
    private boolean url;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public boolean isUrl()
    {
        return url;
    }

    public void setUrl(boolean url)
    {
        this.url = url;
    }
}
