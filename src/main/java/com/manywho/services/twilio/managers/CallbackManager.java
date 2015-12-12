package com.manywho.services.twilio.managers;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.EngineInitializationRequest;
import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.sdk.entities.run.EngineInvokeRequest;
import com.manywho.sdk.entities.run.EngineInvokeResponse;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.map.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.entities.run.elements.ui.*;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.entities.TenantInvokeResponseTuple;
import com.manywho.services.twilio.services.CallbackMessageService;
import com.manywho.services.twilio.services.CallbackVoiceService;
import com.manywho.services.twilio.services.TwilioComponentService;
import com.manywho.services.twilio.types.*;
import com.twilio.sdk.verbs.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class CallbackManager {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    public static final String BASE_CALLBACK_LOCATION = "https://manywhoservices.ngrok.io";

    @Inject
    private CacheManager cacheManager;

    @Inject
    private CallbackMessageService callbackMessageService;

    @Inject
    private CallbackVoiceService callbackVoiceService;

    @Inject
    private TwilioComponentService twilioComponentService;

    public void processMessage(String accountSid, String messageSid, String messageStatus, String errorCode) throws Exception {
        String errorMessage = null;
        String waitMessage = null;

        LOGGER.debug("Received a message callback for the SID {} with the status {}", messageSid, messageStatus);

        if (messageStatus.equalsIgnoreCase("queued")) {
            waitMessage = "The message is currently queued to be sent";
        }

        if (messageStatus.equalsIgnoreCase("sending")) {
            waitMessage = "The message is currently sending";
        }

        if (messageStatus.equalsIgnoreCase("delivered")) {
            waitMessage = "The message has been delivered";
        }

        if (messageStatus.equalsIgnoreCase("undelivered")) {
            errorMessage = "The message was not able to be delivered. Error code: " + errorCode;
        }

        if (messageStatus.equalsIgnoreCase("failed")) {
            errorMessage = "The message failed to send. Error code: " + errorCode;
        }

        ServiceRequest request = cacheManager.getMessageRequest(accountSid, messageSid);

        // Send the callback back to ManyWho, with any WAIT messages or error messages
        InvokeType response = callbackMessageService.sendMessageResponse(request, InvokeType.Forward, errorMessage, errorMessage);

        // If the message has been sent, and the Engine is waiting, assume we're waiting for a reply
        if (messageStatus.equalsIgnoreCase("sent") && response.equals(InvokeType.Wait)) {
            callbackMessageService.sendMessageResponse(request, InvokeType.Forward, "Waiting for a reply to the SMS", null);
        }

        // TODO: Not sure what to do here if the message isn't successful
    }

    public void processMessageReply(String accountSid, String messageSid, String from, String to, String body) throws Exception {
        ServiceRequest request = cacheManager.getMessageRequest(accountSid, to + from);

        InvokeType responseInvokeType = callbackMessageService.sendMessageReplyResponse(request, messageSid, from, body);

        // Only delete the requests if the flow progresses
        //if (!responseInvokeType.equals(InvokeType.Wait)) {
        //    cacheManager.deleteMessageRequest(accountSid, to + from);
        //    cacheManager.deleteMessageRequest(accountSid, messageSid);
        //}
    }

    public void sendCallResponse(String callSid, String answeredBy) throws Exception {
        ServiceRequest request = cacheManager.getCallRequest(callSid);

        InvokeType responseInvokeType = callbackVoiceService.sendCallResponse(request, answeredBy);

        if (responseInvokeType.equals(InvokeType.Success) || responseInvokeType.equals(InvokeType.TokenCompleted)) {
            // Delete the stored request as the state is completed
            cacheManager.deleteCallRequest(callSid);
        }
    }

    EngineInvokeResponse progressToNextStep(String tenantId, EngineInvokeResponse currentStepInvoke, OutcomeResponse outcomeToFollow, PageComponentInputResponseRequestCollection inputs) throws Exception {
        PageRequest pageRequest = new PageRequest();
        if (inputs == null) {
            // Check if there are any components in the returned Page Response, as we need to send one in the next invoke
            Optional<PageComponentResponse> firstComponent = currentStepInvoke.getMapElementInvokeResponses().get(0).getPageResponse().getPageComponentResponses().stream().findFirst();
            if (!firstComponent.isPresent()) {
                throw new Exception("There are no components in the current step");
            }

            pageRequest.setPageComponentInputResponses(new PageComponentInputResponseRequestCollection() {{
                add(new PageComponentInputResponseRequest() {{
                    setPageComponentId(firstComponent.get().getId());
                }});
            }});
        } else {
            pageRequest.setPageComponentInputResponses(inputs);
        }

        EngineInvokeRequest invokeRequest = new EngineInvokeRequest();
        invokeRequest.setCurrentMapElementId(currentStepInvoke.getCurrentMapElementId());
        invokeRequest.setInvokeType(InvokeType.Forward);
        invokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest() {{
            setPageRequest(pageRequest);
            setSelectedOutcomeId(outcomeToFollow.getId());
        }});
        invokeRequest.setStateId(currentStepInvoke.getStateId());
        invokeRequest.setStateToken(currentStepInvoke.getStateToken());

        return new RunService().executeFlow(null, null, tenantId, invokeRequest);
    }

    public String continueFlowAsTwiml(String stateId, String callSid, String digits, String recordingUrl) throws Exception {
        if (cacheManager.hasFlowExecution(stateId, callSid)) {
            TenantInvokeResponseTuple tuple = cacheManager.getFlowExecution(stateId, callSid);
            EngineInvokeResponse invokeResponse = tuple.getInvokeResponse();

            if (invokeResponse.getInvokeType().equals(InvokeType.Wait)) {
                EngineInvokeRequest invokeRequest = new EngineInvokeRequest();
                invokeRequest.setCurrentMapElementId(invokeResponse.getCurrentMapElementId());
                invokeRequest.setInvokeType(InvokeType.Sync);
                invokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest() {{
                    setPageRequest(null);
                }});
                invokeRequest.setStateId(invokeResponse.getStateId());
                invokeRequest.setStateToken(invokeResponse.getStateToken());

                invokeResponse = new RunService().executeFlow(null, null, tuple.getTenantId(), invokeRequest);
            }

            if (invokeResponse.getInvokeType().equals(InvokeType.Wait)) {
                return getWaitTwimlResponse(10, invokeResponse).toXML();
            }

            MapElementInvokeResponse mapElementInvokeResponse = invokeResponse.getMapElementInvokeResponses().get(0);
            PageResponse pageResponse = mapElementInvokeResponse.getPageResponse();

            OutcomeResponseCollection outcomes = mapElementInvokeResponse.getOutcomeResponses();

            // If there are no outcomes, then just speak the current step as normal
            if (CollectionUtils.isEmpty(outcomes)) {
                return createTwimlResponseFromPage(stateId, mapElementInvokeResponse).toXML();
            }

            Optional<OutcomeResponse> outcomeForDigits = outcomes.stream()
                    .filter(outcomeResponse -> outcomeResponse.getDeveloperName().equals(digits))
                    .findFirst();

            List<String> inputComponentTypes = Arrays.asList("INPUT", "Record");

            boolean inputsExist = pageResponse.getPageComponentResponses().stream().anyMatch(component -> inputComponentTypes.contains(component.getComponentType()));

            // If there are no inputs, and the digits don't match with an outcome then say in the TwiML response
            if (!outcomeForDigits.isPresent() && !inputsExist) {
                TwiMLResponse errorResponse = new TwiMLResponse();
                errorResponse.append(new Say("An invalid number was given. Please try again."));

                return this.generateTwimlForInvoke(tuple.getTenantId(), callSid, invokeResponse, errorResponse).toXML();
            }

            PageComponentInputResponseRequestCollection inputs = new PageComponentInputResponseRequestCollection();
            if (inputsExist) {
                // Only fill in the first input, as we can't match up the digits with the desired one if there's more than 1
                Optional<PageComponentResponse> inputComponent = pageResponse.getPageComponentResponses().stream()
                        .filter(component -> component.getComponentType().equals("INPUT"))
                        .findFirst();

                if (inputComponent.isPresent()) {
                    PageComponentInputResponseRequest inputRequest = new PageComponentInputResponseRequest();
                    inputRequest.setPageComponentId(inputComponent.get().getId());
                    inputRequest.setContentValue(digits);

                    inputs.add(inputRequest);
                }
            }

            // If there is still no outcome then just get the first one
            if (!outcomeForDigits.isPresent()) {
                outcomeForDigits = outcomes.stream().findFirst();
            }

            if (pageResponse.getPageComponentResponses().stream().anyMatch(component -> component.getComponentType().equals("Record"))) {
                if (cacheManager.hasRecordingCallback(stateId, callSid) ||
                        (recordingUrl != null &&
                         recordingUrl.isEmpty() == false)) {
                    Optional<PageComponentResponse> voiceComponent = pageResponse.getPageComponentResponses().stream()
                            .filter(component -> component.getComponentType().equals("Record"))
                            .findFirst();

                    if (voiceComponent.isPresent()) {
                        PageComponentResponse voiceComponentResponse = voiceComponent.get();
                        final String recordingIdentifier;
                        final String recordingTranscription;
                        final String recordingUri;

                        if (recordingUrl != null && recordingUrl.isEmpty() == false) {
                            // As this is not a recording callback, we don't have an identifier
                            recordingIdentifier = UUID.randomUUID().toString();
                            recordingTranscription = null;
                            recordingUri = recordingUrl;
                        } else {
                            RecordingCallback recordingCallback = cacheManager.getRecordingCallback(stateId, callSid);
                            recordingIdentifier = recordingCallback.getSid();
                            recordingTranscription = recordingCallback.getTranscription();
                            recordingUri = recordingCallback.getRecordingUrl();
                        }

                        if (voiceComponentResponse.getAttributes() != null &&
                                voiceComponent.get().getAttributes().containsKey("transcribe")) {
                            // Check to see if the user wants transcription - if they do, we need to wait for any transcription text
                            // as Twilio will return "(blank)" even if it captures nothing
                            if (Boolean.parseBoolean(voiceComponent.get().getAttributes().get("transcribe")) &&
                                    (recordingTranscription == null || recordingTranscription.isEmpty() == true)) {
                                // We want a transcription, but we don't have it yet
                                return getWaitTwimlResponse(10, invokeResponse).toXML();
                            }
                        }

                        PageComponentInputResponseRequest inputRequest = new PageComponentInputResponseRequest();
                        inputRequest.setPageComponentId(voiceComponentResponse.getId());
                        inputRequest.setObjectData(new ObjectCollection() {{
                            add(new Object() {{
                                setDeveloperName(Recording.NAME);
                                setExternalId(recordingIdentifier);
                                setProperties(new PropertyCollection() {{
                                    add(new Property(Recording.PROPERTY_URL, recordingUri));
                                    add(new Property(Recording.PROPERTY_TRANSCRIPTION, recordingTranscription));
                                }});
                            }});
                        }});

                        inputs.add(inputRequest);
                    }

                    cacheManager.deleteRecordingCallback(stateId, callSid);
                } else {
                    return getWaitTwimlResponse(10, invokeResponse).toXML();
                }
            }

            EngineInvokeResponse nextStepInvoke = progressToNextStep(tuple.getTenantId(), invokeResponse, outcomeForDigits.get(), inputs);

            cacheManager.saveFlowExecution(invokeResponse.getStateId(), callSid, new TenantInvokeResponseTuple(tuple.getTenantId(), nextStepInvoke));

            if (nextStepInvoke.getInvokeType().equals(InvokeType.Wait)) {
                return getWaitTwimlResponse(10, nextStepInvoke).toXML();
            }

            MapElementInvokeResponse nextMapElementInvokeResponse = nextStepInvoke.getMapElementInvokeResponses().get(0);

            return createTwimlResponseFromPage(stateId, nextMapElementInvokeResponse).toXML();
        }

        return null;
    }

    private TwiMLResponse getWaitTwimlResponse(int pause, EngineInvokeResponse engineInvokeResponse) throws Exception {
        TwiMLResponse waitResponse = new TwiMLResponse();
        waitResponse.append(new Say(engineInvokeResponse.getWaitMessage()));
        waitResponse.append(twilioComponentService.createPauseComponent(pause));
        waitResponse.append(twilioComponentService.createRedirectComponent(BASE_CALLBACK_LOCATION + "/api/twilio/2/callback/twiml/voice/flow/state/" + engineInvokeResponse.getStateId()));

        return waitResponse;
    }

    private EngineInvokeResponse tellFlowToWait(String tenantId, EngineInvokeResponse invokeResponse) throws Exception {
        EngineInvokeRequest invokeRequest = new EngineInvokeRequest();
        invokeRequest.setCurrentMapElementId(invokeResponse.getCurrentMapElementId());
        invokeRequest.setInvokeType(InvokeType.Wait);
        invokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest() {{
            setPageRequest(null);
        }});
        invokeRequest.setStateId(invokeResponse.getStateId());
        invokeRequest.setStateToken(invokeResponse.getStateToken());

        return new RunService().executeFlow(null, null, tenantId, invokeRequest);
    }

    public String startFlowAsTwiml(String tenantId, String flowId, String callSid) throws Exception {
        // Generate the TwiML for the call
        EngineInvokeResponse invokeResponse = this.startFlow(tenantId, flowId);

        TwiMLResponse twiMLResponse = new TwiMLResponse();

        twiMLResponse = this.generateTwimlForInvoke(tenantId, callSid, invokeResponse, twiMLResponse);

        return twiMLResponse.toXML();
    }



    private EngineInvokeResponse startFlow(String tenantId, String flowId) throws Exception {
        EngineInitializationRequest initializationRequest = new EngineInitializationRequest();
        initializationRequest.setFlowId(new FlowId(flowId));
        initializationRequest.setMode("");

        EngineInitializationResponse response = new RunService().initializeFlow(null, null, tenantId, initializationRequest);

        EngineInvokeRequest invokeRequest = new EngineInvokeRequest();
        invokeRequest.setCurrentMapElementId(response.getCurrentMapElementId());
        invokeRequest.setInvokeType(InvokeType.Forward);
        invokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest() {{
            setSelectedOutcomeId(null);
        }});
        invokeRequest.setStateId(response.getStateId());
        invokeRequest.setStateToken(response.getStateToken());

        return new RunService().executeFlow(null, null, tenantId, invokeRequest);
    }

    private TwiMLResponse generateTwimlForInvoke(String tenantId, String callSid, EngineInvokeResponse invokeResponse, final TwiMLResponse twiMLResponse) throws Exception {
        cacheManager.saveFlowExecution(invokeResponse.getStateId(), callSid, new TenantInvokeResponseTuple(tenantId, invokeResponse));

        MapElementInvokeResponse mapElementInvokeResponse = invokeResponse.getMapElementInvokeResponses().get(0);
        PageResponse pageResponse = mapElementInvokeResponse.getPageResponse();

        // Check if there are any components in the returned Page Response, as we need to send one in the next invoke
        Optional<PageComponentResponse> firstComponent = pageResponse.getPageComponentResponses().stream().findFirst();
        if (!firstComponent.isPresent()) {
            throw new Exception("There are no components in the current step");
        }

        // Look for any TwiML containers (i.e. Gather)
//        pageResponse.getPageContainerResponses().stream()
//                .filter(container -> container.getDeveloperName().equals("Twilio.Twiml.Response"))
//                .filter(container -> container.getPageContainerResponses() != null)
//                .forEach(parentContainer -> {
//                    parentContainer.getPageContainerResponses().stream()
//                            .forEach(Throwing.consumer(container -> {
//                                Stream<PageComponentResponse> components = pageResponse.getPageComponentResponses().stream()
//                                        .filter(component -> component.getPageContainerId().equals(container.getId()));
//
//                                switch (container.getContainerType()) {
//                                    case "Gather":
//                                        Gather gather = new Gather();
//                                        gather.setAction(BASE_CALLBACK_LOCATION + "/api/twilio/2/callback/twiml/voice/flow/state/" + invokeResponse.getStateId());
//
//                                        for (Verb verb : createTwimlForComponents(pageResponse, components)) {
//                                            gather.append(verb);
//                                        }
//
//                                        twiMLResponse.append(gather);
//                                }
//                            }));
//                });

        return createTwimlResponseFromPage(invokeResponse.getStateId(), mapElementInvokeResponse);
    }

    private TwiMLResponse createTwimlResponseFromPage(String stateId, MapElementInvokeResponse mapElementInvokeResponse) throws TwiMLException {
        PageResponse pageResponse = mapElementInvokeResponse.getPageResponse();

        TwiMLResponse twiMLResponse = new TwiMLResponse();

        List<Verb> twimlComponents = pageResponse.getPageComponentResponses().stream()
                .map(component -> twilioComponentService.createTwimlForComponent(pageResponse, component, stateId))
                .collect(Collectors.toList());

        // Add all the non-null TwiML components to the response
        twimlComponents.stream()
                .filter(Objects::nonNull)
                .forEach(component -> twiMLResponse.getChildren().add(component));

        // If there are 2 or more outcomes for the current map element, generate a Gather
        if (mapElementInvokeResponse.hasOutcomeResponses()) {
            List<String> outcomeNames = mapElementInvokeResponse.getOutcomeResponses().stream()
                    .map(OutcomeAvailable::getDeveloperName)
                    .collect(Collectors.toList());

            Gather gather = new Gather();
            gather.setAction(BASE_CALLBACK_LOCATION + "/api/twilio/2/callback/twiml/voice/flow/state/" + stateId);

            // If there aren't already any TwiML components, then add a Say component to the Gather
            if (twiMLResponse.getChildren().isEmpty()) {
                gather.append(new Say("Please choose a number from the following: " + StringUtils.join(outcomeNames, ", ")));
            }

            twiMLResponse.append(gather);
        }

        return twiMLResponse;
    }

    public void saveRecordingCallback(String stateId, RecordingCallback recordingCallback) throws Exception {
        cacheManager.saveRecordingCallback(stateId, recordingCallback.getCallSid(), recordingCallback);
    }
}
