package com.manywho.services.twilio.managers;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.manywho.services.twilio.entities.MessageCallback;
import javax.inject.Inject;

public class WebhookManager {
    private CallbackManager callbackManager;

    @Inject
    public WebhookManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    public void handleMessageStatus(MessageCallback callback) throws Exception {
        if (callback.getSmsStatus() != null && callback.getSmsStatus().equalsIgnoreCase("received")) {
            String from = internationalFormatE164(callback.getFrom(), callback.getFromCountry());
            String to = internationalFormatE164(callback.getTo(), callback.getToCountry());

            callbackManager.processMessageReply(
                    callback.getAccountSid(),
                    callback.getMessageSid(),
                    from,
                    to,
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

    public static String internationalFormatE164(String number, String country) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, country);

        return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
