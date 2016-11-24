package com.weizilla.garmin.fetcher;

import com.weizilla.garmin.fetcher.request.RequestFactory;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class RequestFactoryStub extends RequestFactory
{
    private final HttpUriRequest request;
    private final boolean extractResponse;

    public RequestFactoryStub(HttpUriRequest request, boolean extractResponse)
    {
        this.request = request;
        this.extractResponse = extractResponse;
    }

    @Override
    public HttpUriRequest create(String prevResult) throws IOException
    {
        return request;
    }

    @Override
    public boolean isExtractResult()
    {
        return extractResponse;
    }

    @Override
    public String getStepName()
    {
        return null;
    }
}
