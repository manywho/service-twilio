package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.services.CallbackService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;

public class CallbackManager {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Inject
    private CacheManager cacheManager;

    @Inject
    private CallbackService callbackService;

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
        InvokeType response = callbackService.sendMessageResponse(request, InvokeType.Forward, waitMessage, errorMessage);

        // If the message has been sent, and the Engine is waiting, assume we're waiting for a reply
        if (messageStatus.equalsIgnoreCase("sent") && response.equals(InvokeType.Wait)) {
            callbackService.sendMessageResponse(request, InvokeType.Forward, "Waiting for a reply to the SMS", null);
        }

        // TODO: Not sure what to do here if the message isn't successful
    }

    public void processMessageReply(String accountSid, String messageSid, String from, String to, String body) throws Exception {
        ServiceRequest request = cacheManager.getMessageRequest(accountSid, to + from);

        InvokeType responseInvokeType = callbackService.sendMessageReplyResponse(request, messageSid, from, body);

        // Only delete the requests if the flow progresses
        if (!responseInvokeType.equals(InvokeType.Wait)) {
            cacheManager.deleteMessageRequest(accountSid, to + from);
            cacheManager.deleteMessageRequest(accountSid, messageSid);
        }
    }

    public void sendCallResponse(String callSid, String answeredBy) throws Exception {
        ServiceRequest request = cacheManager.getCallRequest(callSid);

        InvokeType responseInvokeType = callbackService.sendCallResponse(request, answeredBy);

        if (responseInvokeType.equals(InvokeType.Success) || responseInvokeType.equals(InvokeType.TokenCompleted)) {
            // Delete the stored request as the state is completed
            cacheManager.deleteCallRequest(callSid);
        }
    }
}
