package com.weizilla.garmin.fetcher.request;

import org.apache.http.client.methods.HttpRequestBase;

public class Request
{
    private final HttpRequestBase httpRequest;
    private final boolean finalStep;
    private final boolean extractResult;

    public Request(HttpRequestBase httpRequest, boolean finalStep, boolean extractResult)
    {
        this.httpRequest = httpRequest;
        this.finalStep = finalStep;
        this.extractResult = extractResult;
    }

    public HttpRequestBase getHttpRequest()
    {
        return httpRequest;
    }

    public boolean isFinalStep()
    {
        return finalStep;
    }

    public boolean isExtractResult()
    {
        return extractResult;
    }
}
