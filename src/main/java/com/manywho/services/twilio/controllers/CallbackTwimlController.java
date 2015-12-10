package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
            @FormParam("Digits") String digits
    ) throws Exception {
        return callbackManager.continueFlowAsTwiml(stateId, callSid, digits);
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
        }
    }
}
