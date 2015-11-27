package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback/status")
public class CallbackStatusController {

    @Inject
    private CallbackManager callbackManager;

    @POST
    @Path("/voice")
    @Consumes("application/x-www-form-urlencoded")
    public void voiceCallback(
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction,
            @FormParam("AnsweredBy") String answeredBy
    ) throws Exception {
        // Only progress if the call is outbound
        if (direction.equals("outbound-api") || direction.equals("outbound-dial")) {
            // Send a response back to ManyWho, updating the state with the current call status
            callbackManager.sendCallResponse(callSid, answeredBy);
        }
    }

    @POST
    @Path("/message")
    @Consumes("application/x-www-form-urlencoded")
    public void messageCallback(@BeanParam MessageCallback callback) throws Exception {
        // If the callback is from a message reply, process it
        if (callback.getSmsStatus() == null || !callback.getSmsStatus().equalsIgnoreCase("received")) {
            callbackManager.processMessage(
                    callback.getAccountSid(),
                    callback.getMessageSid(),
                    callback.getMessageStatus(),
                    callback.getErrorCode()
            );
        }
    }
}
