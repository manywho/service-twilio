package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.managers.CallbackTwimlManager;

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
            return callbackTwimlManager.startFlowAsTwiml(tenantId, flowId, callSid).toXML();
        }

        return null;
    }

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
                callbackTwimlManager.continueFlowAsTwiml(stateId, callSid, digits, recordingUrl).toXML()
        );
    }
}
