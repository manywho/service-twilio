package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback/twiml")
public class CallbackTwimlController {

    @Inject
    private CallbackManager callbackManager;

    @POST
    @Path("/voice/flow/{tenantId}/{flowId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceFlowCallback(
            @PathParam("tenantId") String tenantId,
            @PathParam("flowId") String flowId,
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction,
            @FormParam("AnsweredBy") String answeredBy
    ) throws Exception {
        if (direction.equals("inbound")) {
            return callbackManager.startFlowAsTwiml(tenantId, flowId, callSid);
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
        return callbackManager.continueFlowAsTwiml(stateId, callSid, digits, recordingUrl);
    }
}
