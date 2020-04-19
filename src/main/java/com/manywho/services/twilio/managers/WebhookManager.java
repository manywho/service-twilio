package com.manywho.services.twilio.managers;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.manywho.services.twilio.entities.MessageCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;

public class WebhookManager {
    private CallbackManager callbackManager;
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Inject
    public WebhookManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    public void handleMessageStatus(MessageCallback callback) throws Exception {
        if (callback.getSmsStatus() != null && callback.getSmsStatus().equalsIgnoreCase("received")) {
            String from = internationalFormatE164(callback.getFrom(), callback.getFromCountry());
            String to = internationalFormatE164(callback.getTo(), callback.getToCountry());

            LOGGER.info("Received a message status from {} to {}, status {}", from, to, callback.getSmsStatus());

            callbackManager.processMessageReply(
                    callback.getAccountSid(),
                    callback.getMessageSid(),
                    from,
                    to,
                    callback.getBody()
            );

            LOGGER.info("Reply processed for message from {} to {}, status {}", from, to, callback.getSmsStatus());

        } else if (callback.getSmsStatus() != null && callback.getSmsStatus().equalsIgnoreCase("delivered")) {
            // we ignore the 'delivered' status (because some carrier don't sent this information), we will process the
            // message in status 'sent' because all the carrier sent this status.
            LOGGER.info("Ignored status deliverd because is not consistent in all carrier");
            return;
        } else {
            LOGGER.info("Process message with status {}", callback.getSmsStatus());
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

    private static String internationalFormatE164(String number, String country) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, country);

        return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
