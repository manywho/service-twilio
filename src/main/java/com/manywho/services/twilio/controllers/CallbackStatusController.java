package com.manywho.services.twilio.controllers;

import com.manywho.sdk.RunService;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.managers.CallbackManager;
import com.manywho.services.twilio.services.FlowService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Path("/callback/status")
public class CallbackStatusController {

    @Inject
    private CallbackManager callbackManager;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private FlowService flowService;

    @Inject
    private RunService runService;

    @POST
    @Path("/voice")
    @Consumes("application/x-www-form-urlencoded")
    public void voiceCallback(
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction,
            @FormParam("AnsweredBy") String answeredBy,
            @FormParam("RecordingSid") String recordingSid
    ) throws Exception {

        // Only progress if the call is outbound
        if (direction.equals("outbound-api") || direction.equals("outbound-dial")) {
            // Send a response back to ManyWho, updating the state with the current call status
            //callbackManager.sendCallResponse(callSid, answeredBy);

            ServiceRequest serviceRequest = cacheManager.getCallRequest(callSid);
            FlowState flowState = cacheManager.getFlowExecution(serviceRequest.getStateId(), callSid);

            if (!cacheManager.isCallHungupByTwiml(callSid)) {
                ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
                serviceResponse.setTenantId(serviceRequest.getTenantId());
                HashMap<String, String > errors = new HashMap<>();
                errors.put("hangup", "the user hangup before the flow finish");
                serviceResponse.setRootFaults(errors);

                runService.sendResponse(null, null, flowState.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
            }

            if(flowState.hasOutcomes()) {
                Optional<Outcome> outcomeForDigits = flowState.getOutcomes().stream().findFirst();

                if (StringUtils.isNotEmpty(recordingSid)) {
                    callbackManager.saveCallRecordingSid(callSid, recordingSid);

                    if (!cacheManager.isCallHungupByTwiml(callSid)) {
                        Optional<Outcome> outcomeForDigitsFail = flowState.getOutcomes().stream()
                                .filter(outcome -> Objects.equals(outcome.getName(), "fail"))
                                .findFirst();

                        if (outcomeForDigits.isPresent()) {
                            outcomeForDigits = outcomeForDigitsFail;
                        }
                    }
                }

                PageComponentInputResponseRequestCollection inputs = new PageComponentInputResponseRequestCollection();
                flowService.progressToNextStep(flowState, outcomeForDigits.get(), inputs, InvokeType.Forward);
            }
        }



        // Only progress if the call is outbound
        //if (direction.equals("outbound-api") || direction.equals("outbound-dial")) {
            // Send a response back to ManyWho, updating the state with the current call status
        //    callbackManager.sendCallResponse(callSid, answeredBy);
        //}
    }

    @POST
    @Path("/message")
    @Consumes("application/x-www-form-urlencoded")
    public void messageCallback(@BeanParam MessageCallback callback) throws Exception {
        // If the callback is from a message reply, process it
        if (callback.getSmsStatus() != null && callback.getSmsStatus().equalsIgnoreCase("received")) {
            callbackManager.processMessageReply(
                    callback.getAccountSid(),
                    callback.getMessageSid(),
                    callback.getFrom(),
                    callback.getTo(),
                    callback.getBody()
            );
        } else if (callback.getSmsStatus() != null && callback.getSmsStatus().equalsIgnoreCase("delivered")) {
            // we ignore the 'delivered' status (because some carrier don't sent this information), we will process the
            // message in status 'sent' because all the carrier sent this status.

            return;
        } else {
            // Otherwise, send back the status
            callbackManager.processMessage(
                    callback.getAccountSid(),
                    callback.getMessageSid(),
                    callback.getMessageStatus(),
                    callback.getErrorCode()
            );
        }
    }
}
