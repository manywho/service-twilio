package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback/twiml")
public class CallbackTwimlController {

    @Inject
    private CallbackManager callbackManager;

    @POST
    @Path("/voice")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceCallback(
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction,
            @FormParam("AnsweredBy") String answeredBy
    ) throws Exception {
        if (direction.equals("outbound-api") || direction.equals("outbound-dial")) {
            // Generate the TwiML for the call

            return "<Response>\n" +
                    "<Say voice=\"alice\">Holla holla from ManyWho, boi</Say>\n" +
                    "</Response>";
        }

        return null;
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
