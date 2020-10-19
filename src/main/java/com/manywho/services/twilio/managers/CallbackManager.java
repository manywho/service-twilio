package com.manywho.services.twilio.managers;

import com.manywho.sdk.client.FlowClient;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.services.CallbackMessageService;
import com.manywho.services.twilio.services.CallbackVoiceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import javax.inject.Inject;
import java.util.UUID;

public class CallbackManager {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());
    private static final String TWILIO_RECORD_URI = "https://api.twilio.com/2010-04-01/Accounts/%s/Recordings/%s";

    @Inject
    private CacheManager cacheManager;

    @Inject
    private CallbackMessageService callbackMessageService;

    @Inject
    private CallbackVoiceService callbackVoiceService;

    @Inject
    private FlowClient flowClient;

    public void processMessage(String accountSid, String messageSid, String messageStatus, String errorCode, MessageCallback callback) throws Exception {
        String errorMessage = null;

        LOGGER.debug("Received a message callback for the SID {} with the status {}", messageSid, messageStatus);

        if (messageStatus.equalsIgnoreCase("undelivered")) {
            errorMessage = "The message was not able to be delivered. Error code: " + errorCode;
        }

        if (messageStatus.equalsIgnoreCase("failed")) {
            errorMessage = "The message failed to send. Error code: " + errorCode;
        }

        ServiceRequest request = cacheManager.getMessageRequest(accountSid, messageSid);
        AuthenticatedWho authenticatedWho = cacheManager.getAuthenticatedWho(accountSid, messageSid);

        // Send the callback back to ManyWho, with any WAIT messages or error messages
        InvokeType response = callbackMessageService.sendMessageResponse(request, authenticatedWho, errorMessage, errorMessage);

        // If the message has been sent, and the Engine is waiting, assume we're waiting for a reply
        if (messageStatus.equalsIgnoreCase("sent") && response.equals(InvokeType.Wait)) {
            cacheManager.stateWaitingForSms(callback.getFrom() + callback.getTo(), request.getStateId());
            callbackMessageService.sendMessageResponse(request, authenticatedWho, "Waiting for a reply to the SMS", null);

            FlowState flowState = flowClient.join(UUID.fromString(request.getTenantId()), UUID.fromString(request.getStateId()), null);
            if(!flowState.getInvokeType().equals(InvokeType.Wait)) {
                cacheManager.deleteStateWaitingForSms(callback.getFrom() + callback.getTo());
            }
        } else if (messageStatus.equalsIgnoreCase("sent")) {
            cacheManager.deleteStateWaitingForSms(callback.getFrom() + callback.getTo());
        }

        // TODO: Not sure what to do here if the message isn't successful
    }

    public void processMessageReply(String accountSid, String messageSid, String from, String to, String body) throws Exception {
        ServiceRequest request = cacheManager.getMessageRequest(accountSid, to + from);
        AuthenticatedWho authenticatedWho = cacheManager.getAuthenticatedWho(accountSid, to + from);

        callbackMessageService.sendMessageReplyResponse(request, authenticatedWho, messageSid, from, to, body);
    }

    public void sendCallResponse(String callSid, String answeredBy) throws Exception {
        ServiceRequest request = cacheManager.getCallRequest(callSid);

        InvokeType responseInvokeType = callbackVoiceService.sendCallResponse(request, answeredBy);

        if (responseInvokeType.equals(InvokeType.Success) || responseInvokeType.equals(InvokeType.TokenCompleted)) {
            // Delete the stored request as the state is completed
            cacheManager.deleteCallRequest(callSid);
        }
    }

    public void saveCallRecordingSid(String callSid, String recordSid) {
        cacheManager.saveCallRecordingSid(callSid, recordSid);
    }

    public String getCallRecordingUrl(String accountSid, String callSid) {
        return String.format(
                TWILIO_RECORD_URI,
                accountSid,
                cacheManager.getCallRecordingSid(callSid)
        );
    }
}
