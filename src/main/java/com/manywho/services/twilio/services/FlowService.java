package com.manywho.services.twilio.services;

import com.manywho.sdk.client.RunClient;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.options.FlowInitializationOptions;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.enums.FlowMode;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

public class FlowService {
    final private RunClient runClient;

    @Inject
    public FlowService(RunClient runClient) {
        this.runClient = runClient;
    }

    public FlowState startFlow(String tenantId, String flowId) throws Exception {
        FlowInitializationOptions initializationOptions = new FlowInitializationOptions()
                .setMode(FlowMode.Default);

        return runClient.startFlow(tenantId, new FlowId(flowId), initializationOptions);
    }

    public FlowState joinFlow(String tenantId, String stateId) throws IOException, URISyntaxException {
        return runClient.joinFlow(tenantId, stateId, null);
    }
}
