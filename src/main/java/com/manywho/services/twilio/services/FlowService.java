package com.manywho.services.twilio.services;

import com.manywho.sdk.client.RunClient;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.sdk.client.options.FlowInitializationOptions;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequest;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.sdk.entities.run.elements.ui.PageRequest;
import com.manywho.sdk.enums.FlowMode;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.managers.CacheManager;

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

    public FlowState progressToNextStep(FlowState flowState, Outcome outcome,
                                        PageComponentInputResponseRequestCollection inputs, InvokeType invokeType) throws Exception {

        PageRequest pageRequest = new PageRequest();

        if (inputs == null) {
            // Check if there are any components in the returned Page Response, as we need to send one in the next invoke
            if (!flowState.hasPageComponents()) {
                throw new Exception("There are no components in the current step");
            }

            pageRequest.addPageComponentInputResponse(new PageComponentInputResponseRequest(flowState.getPageComponents().get(0).getId()));
        } else {
            pageRequest.setPageComponentInputResponses(inputs);
        }

        // If we want to do a SYNC, then perform the SYNC. Otherwise we progress the flow with the selected outcome and inputs
        if (invokeType == InvokeType.Sync) {
            flowState.sync();
        } else {
            flowState.selectOutcome(outcome, pageRequest);
        }

        return flowState;
    }

    public void syncIfStatusWait(FlowState flowState) throws IOException, URISyntaxException {
        // If the flow currently has a WAIT status, then SYNC to see if the status has changed
        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            flowState.sync();
        }
    }


}
