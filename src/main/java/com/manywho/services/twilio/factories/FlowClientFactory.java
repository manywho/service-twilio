package com.manywho.services.twilio.factories;

import com.manywho.sdk.client.FlowClient;
import com.manywho.sdk.client.raw.RawRunClient;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class FlowClientFactory implements Factory<FlowClient>{
    @Inject
    private RawRunClient rawRunClient;

    @Override
    public FlowClient provide() {
        return new FlowClient(rawRunClient);
    }

    @Override
    public void dispose(FlowClient flowClient) {

    }
}
