package com.manywho.services.twilio.services;

import com.manywho.sdk.client.FlowClient;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.sdk.client.options.FlowInitializationOptions;
import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.*;
import com.manywho.sdk.entities.run.elements.map.MapElementInvokeRequest;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequest;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.sdk.entities.run.elements.ui.PageRequest;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.FlowMode;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.types.SmsWebhook;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

public class FlowService {
    final private RawRunClient runClient;
    private FlowClient flowClient;

    @Inject
    public FlowService(RawRunClient runClient) {
        this.runClient = runClient;
        this.flowClient = new FlowClient(this.runClient);
    }

    public FlowState startFlow(String tenantId, String flowId) throws Exception {
        FlowInitializationOptions initializationOptions = new FlowInitializationOptions()
                .setMode(FlowMode.Default);

        return this.flowClient.start(UUID.fromString(tenantId), new FlowId(flowId), initializationOptions);
    }

    public FlowState joinFlow(String tenantId, String stateId) throws IOException, URISyntaxException {
        return this.flowClient.join(UUID.fromString(tenantId), UUID.fromString(stateId), null);
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

    public void initializeAndExecuteFlow(String flowId, String flowVersionId, String tenantId, MessageCallback smsWebhook) throws Exception {
        try {
            EngineInitializationResponse flow;
            flow = this.initializeFlowWithoutAuthentication(flowId, flowVersionId, tenantId, smsWebhook.getMessageSid());
            EngineInvokeRequest engineInvokeRequest = new EngineInvokeRequest();
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setInvokeType(InvokeType.Forward);
            engineInvokeRequest.setStateToken(flow.getStateToken());
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setCurrentMapElementId(flow.getCurrentMapElementId());
            engineInvokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest());

            EngineInvokeResponse engineInvokeResponse = this.executeFlow(tenantId, null, engineInvokeRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    private EngineInitializationResponse initializeFlowWithoutAuthentication(String flowId, String flowVersionId, String tenantId, String messageSid) throws Exception {

        EngineInitializationRequest engineInitializationRequest = new EngineInitializationRequest();
        engineInitializationRequest.setMode(FlowMode.Default.toString());

        if(flowVersionId == null || Objects.equals(flowVersionId, "")) {
            engineInitializationRequest.setFlowId(new FlowId(flowId));
        } else {
            engineInitializationRequest.setFlowId(new FlowId(flowId, flowVersionId));
        }

        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("Webhook Message Sid", ContentType.String, messageSid));
        engineInitializationRequest.setInputs(engineValues);

        return runClient.initialize(UUID.fromString(tenantId), null, engineInitializationRequest);
    }

    private EngineInvokeResponse executeFlow(String tenantId, String auth, EngineInvokeRequest engineInvokeRequest) {
        return runClient.execute(UUID.fromString(tenantId), auth, engineInvokeRequest);
    }
}
