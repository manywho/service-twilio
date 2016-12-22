package com.manywho.services.twilio.managers;


import com.manywho.services.twilio.entities.MessageCallback;
import javax.inject.Inject;

public class WebhookManager {
    private CallbackManager callbackManager;
    private CacheManager cacheManager;

    @Inject
    public WebhookManager(CallbackManager callbackManager, CacheManager cacheManager) {
        this.callbackManager = callbackManager;
        this.cacheManager = cacheManager;
    }

    public void handleMessageStatus(MessageCallback callback) throws Exception {
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
                    callback.getErrorCode(),
                    callback
            );
        }
    }
}
