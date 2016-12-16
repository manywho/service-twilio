package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.managers.CallbackTwimlManager;
import com.manywho.services.twilio.services.twiml.TwilioComponentService;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback/callbackTwiml")
public class CallbackTwimlController {

    @Inject
    private CallbackTwimlManager callbackTwimlManager;

    @POST
    @Path("/voice/flow/{tenantId}/{flowId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceFlowCallback(
            @PathParam("tenantId") String tenantId,
            @PathParam("flowId") String flowId,
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction
    ) throws Exception {
        if (direction.equals("inbound")) {
            return callbackTwimlManager.startFlowAsTwiml(tenantId, flowId, callSid, TwilioComponentService.CallbackType.PHONE_CALL_CALLBACK).toXML();
        }

        return null;
    }

    /**
     *
     * This entry point will return a twiml (xml) when is called by twilio,
     * When we are waiting for some information, the response will have a child <Pause length="x"/> and this entry point
     * will be called in x seconds by twilio again, then we can update the xml with the new information (if the information
     * is not ready we will return again a pause child).
     *
     * When the call finish this entry point is not called any more.
     */
    @POST
    @Path("/voice/flow/state/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceFlowStateCallback(
            @PathParam("stateId") String stateId,
            @FormParam("CallSid") String callSid,
            @FormParam("Digits") String digits,
            @FormParam("RecordingUrl") String recordingUrl
    ) throws Exception {

        return String.format("%s%s",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                callbackTwimlManager.continueFlowAsTwiml(stateId, callSid, digits, recordingUrl, TwilioComponentService.CallbackType.PHONE_CALL_CALLBACK).toXML()
        );
    }

    @POST
    @Path("/sms/flow/state/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String smsFlowStateCallback(
            @PathParam("stateId") String stateId,
            @FormParam("CallSid") String callSid,
            @FormParam("Digits") String digits,
            @FormParam("RecordingUrl") String recordingUrl
    ) throws Exception {

        return String.format("%s%s",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                callbackTwimlManager.continueFlowAsTwiml(stateId, callSid, digits, recordingUrl, TwilioComponentService.CallbackType.SMS_CALLBACK).toXML()
        );
    }

    @POST
    @Path("/sms/flow/{tenantId}/{flowId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String smsFlowCallback(
            @PathParam("tenantId") String tenantId,
            @PathParam("flowId") String flowId,
            @FormParam("From") String from,
            @FormParam("To") String to,
            @FormParam("Body") String body,
            @FormParam("MessageSid") String messageSid,
            @FormParam("SmsSid") String smsSid,
            @FormParam("FromCountry") String fromCountry,
            @FormParam("ToCountry") String toCountry,
            @FormParam("Direction") String direction
    ) throws Exception {
        return String.format("%s%s",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                callbackTwimlManager.startFlowAsTwiml(tenantId, flowId, messageSid, TwilioComponentService.CallbackType.SMS_CALLBACK).toXML());
    }
}
