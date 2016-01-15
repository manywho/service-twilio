package com.manywho.services.twilio.managers;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.sdk.client.entities.PageComponent;
import com.manywho.sdk.entities.run.EngineInvokeResponse;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequest;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.sdk.entities.run.elements.ui.PageRequest;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.services.FlowService;
import com.manywho.services.twilio.services.ObjectMapperService;
import com.manywho.services.twilio.services.TwilioComponentService;
import com.manywho.services.twilio.types.Recording;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.Verb;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.stream.Collectors;

import static com.manywho.sdk.client.utils.PageComponentUtils.doInputComponentsExist;
import static com.manywho.sdk.client.utils.PageComponentUtils.doesComponentWithTypeExist;
import static com.manywho.sdk.client.utils.PageComponentUtils.getFirstComponentWithType;

public class CallbackTwimlManager {
    final private static List<String> inputComponentTypes = Collections.singletonList("Record");

    @Context
    private UriInfo uriInfo;

    final private TwilioComponentService twilioComponentService;
    final private CacheManager cacheManager;
    final private FlowService flowService;
    final private ObjectMapperService objectMapperService;

    @Inject
    public CallbackTwimlManager(TwilioComponentService twilioComponentService, CacheManager cacheManager, FlowService flowService, ObjectMapperService objectMapperService) {
        this.twilioComponentService = twilioComponentService;
        this.cacheManager = cacheManager;
        this.flowService = flowService;
        this.objectMapperService = objectMapperService;
    }

    private FlowState progressToNextStep(String callSid, FlowState flowState, Outcome outcome, PageComponentInputResponseRequestCollection inputs, InvokeType invokeType) throws Exception {
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

        // Updated the stored flow state with the new state
        cacheManager.saveFlowExecution(flowState.getStateId(), callSid, flowState);

        return flowState;
    }

    public TwiMLResponse continueFlowAsTwiml(String stateId, String callSid, String digits, String recordingUrl) throws Exception {
        if (cacheManager.hasFlowExecution(stateId, callSid)) {
            // If we have a stored flow state then create TwiML based on that

            return createTwimlForNormalFlow(callSid, stateId, digits, recordingUrl);
        } else if (cacheManager.hasCallRequest(callSid)) {
            // If we have a call request stored, create TwiML based on that

            return createTwimlForCallRequest(callSid, stateId);
        }

        throw new Exception("Unable to continue the flow as no stored requests are found for the SID " + callSid);
    }

    private TwiMLResponse createTwimlForNormalFlow(String callSid, String stateId, String digits, String recordingUrl) throws Exception {
        FlowState flowState = cacheManager.getFlowExecution(stateId, callSid);

        // If the flow currently has a WAIT status, then SYNC to see if the status has changed
        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            flowState.sync();
        }

        // If the flow still has a WAIT status, then return some TwiML to say that we're still waiting
        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            return createTwimlResponseFromWait(10, flowState.getInvokeResponse());
        }

        // If there are no outcomes, then just speak the current step as normal
        if (!flowState.hasOutcomes()) {
            return createTwimlResponseFromPage(stateId, flowState);
        }

        Optional<Outcome> outcomeForDigits = flowState.getOutcomes().stream()
                .filter(outcome -> outcome.getName().equals(digits))
                .findFirst();

        boolean inputsExist = doInputComponentsExist(flowState.getPageComponents(), inputComponentTypes);

        // If there are no inputs, and the digits don't match with an outcome then repeat the current step as TwiML
        if (!outcomeForDigits.isPresent() && !inputsExist) {
            return this.generateTwimlForInvoke(callSid, flowState);
        }

        // If there is still no outcome then just get the first one
        if (!outcomeForDigits.isPresent()) {
            outcomeForDigits = flowState.getOutcomes().stream().findFirst();
        }

        PageComponentInputResponseRequestCollection inputs = new PageComponentInputResponseRequestCollection();
        if (inputsExist) {
            // Only fill in the first input, as we can't match up the digits with the desired one if there's more than 1
            Optional<PageComponent> inputComponent = getFirstComponentWithType(flowState.getPageComponents(), "INPUT");
            if (inputComponent.isPresent()) {
                inputs.add(new PageComponentInputResponseRequest(inputComponent.get().getId(), digits));
            }
        }

        if (doesComponentWithTypeExist(flowState.getPageComponents(), "Record")) {
            if (cacheManager.hasRecordingCallback(stateId, callSid) || StringUtils.isNotEmpty(recordingUrl)) {
                Optional<PageComponent> voiceComponent = getFirstComponentWithType(flowState.getPageComponents(), "Record");

                if (voiceComponent.isPresent()) {
                    Recording recording = new Recording();

                    if (StringUtils.isNotEmpty(recordingUrl)) {
                        // As this is not a recording callback, we don't have an identifier
                        recording.setId(UUID.randomUUID().toString());
                        recording.setUrl(recordingUrl);
                    } else {
                        RecordingCallback recordingCallback = cacheManager.getRecordingCallback(stateId, callSid);

                        recording.setId(recordingCallback.getSid());
                        recording.setTranscription(recordingCallback.getTranscription());
                        recording.setUrl(recordingCallback.getRecordingUrl());
                    }

                    if (voiceComponent.get().getAttributes().containsKey("transcribe")) {
                        // Check to see if the user wants transcription - if they do, we need to wait for any transcription text
                        // as Twilio will return "(blank)" even if it captures nothing
                        if (Boolean.parseBoolean(voiceComponent.get().getAttributes().get("transcribe")) &&
                                StringUtils.isEmpty(recording.getTranscription())) {
                            // We want a transcription, but we don't have it yet
                            return createTwimlResponseFromWait(10, flowState.getInvokeResponse());
                        }
                    }

                    PageComponentInputResponseRequest inputRequest = new PageComponentInputResponseRequest();
                    inputRequest.setPageComponentId(voiceComponent.get().getId());
                    inputRequest.setObjectData(new ObjectCollection(
                            objectMapperService.convertRecordingToObject(recording)
                    ));

                    inputs.add(inputRequest);
                }

                cacheManager.deleteRecordingCallback(stateId, callSid);
            } else {
                return createTwimlResponseFromWait(10, flowState.getInvokeResponse());
            }
        }

        FlowState nextStepState = progressToNextStep(callSid, flowState, outcomeForDigits.get(), inputs, InvokeType.Forward);

        if (nextStepState.getInvokeType().equals(InvokeType.Wait)) {
            return createTwimlResponseFromWait(10, nextStepState.getInvokeResponse());
        }

        return createTwimlResponseFromPage(stateId, nextStepState);
    }

    private TwiMLResponse createTwimlForCallRequest(String callSid, String stateId) throws Exception {
        // Fetch the cached ServiceRequest for the given Call SID
        ServiceRequest serviceRequest = cacheManager.getCallRequest(callSid);

        // Join the flow as we won't have executed it yet in the context of this service
        FlowState flowState = flowService.joinFlow(serviceRequest.getTenantId(), stateId);

        cacheManager.saveFlowExecution(flowState.getStateId(), callSid, flowState);

        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            return createTwimlResponseFromWait(10, flowState.getInvokeResponse());
        }

        return createTwimlResponseFromPage(stateId, flowState);
    }

    private String createTwimlVoiceStateUrl(String stateId) {
        return "https://" + uriInfo.getBaseUri().getHost() + uriInfo.getBaseUri().getPath() + "callback/twiml/voice/flow/state/" + stateId;
    }

    private TwiMLResponse createTwimlResponseFromWait(int pause, EngineInvokeResponse engineInvokeResponse) throws Exception {
        TwiMLResponse waitResponse = new TwiMLResponse();
        waitResponse.append(new Say(engineInvokeResponse.getWaitMessage()));
        waitResponse.append(twilioComponentService.createPauseComponent(pause));
        waitResponse.append(twilioComponentService.createRedirectComponent(createTwimlVoiceStateUrl(engineInvokeResponse.getStateId())));

        return waitResponse;
    }

    public TwiMLResponse startFlowAsTwiml(String tenantId, String flowId, String callSid) throws Exception {
        // Generate the TwiML for the call
        FlowState flowState = flowService.startFlow(tenantId, flowId);

        return this.generateTwimlForInvoke(callSid, flowState);
    }

    private TwiMLResponse generateTwimlForInvoke(String callSid, FlowState flowState) throws Exception {
        cacheManager.saveFlowExecution(flowState.getStateId(), callSid, flowState);

        // Check if there are any components in the returned Page Response, as we need to send one in the next invoke
        if (!flowState.hasPageComponents()) {
            throw new Exception("There are no components in the current step");
        }

        return createTwimlResponseFromPage(flowState.getStateId(), flowState);
    }

    private TwiMLResponse createTwimlResponseFromPage(String stateId, FlowState flowState) throws TwiMLException {
        TwiMLResponse twiMLResponse = new TwiMLResponse();

        // Create TwiML components from all the PageComponents
        List<Verb> twimlComponents = flowState.getPageComponents().stream()
                .map(component -> twilioComponentService.createTwimlForComponent(component, stateId))
                .collect(Collectors.toList());

        // If there are outcomes and we should auto wrap in a gather, then we do that
        if (flowState.hasOutcomes() && !doesComponentWithTypeExist(flowState.getPageComponents(), "Record")) {
            Gather gather = new Gather();
            gather.setAction(createTwimlVoiceStateUrl(stateId));

            Optional<Outcome> longestNamedOutcome = flowState.getOutcomes().stream().max(Comparator.comparing(outcome -> outcome.getName().length()));

            if(longestNamedOutcome.isPresent()) {
                gather.setNumDigits(longestNamedOutcome.get().getName().length());
            }

            // Add all the TwiML components to the Gather
            twimlComponents.stream()
                    .forEach(component -> gather.getChildren().add(component));

            twiMLResponse.append(gather);

            // Automatically append a pause and join in case they need to re-hear the message
            twiMLResponse.append(twilioComponentService.createPauseComponent(10));
            twiMLResponse.append(twilioComponentService.createRedirectComponent(createTwimlVoiceStateUrl(stateId)));
        } else {
            // Add all the non-null TwiML components to the response
            twimlComponents.stream()
                    .forEach(component -> twiMLResponse.getChildren().add(component));
        }

        return twiMLResponse;
    }
}
