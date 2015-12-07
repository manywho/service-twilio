package com.manywho.services.twilio.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.twilio.entities.types.Sms;

import javax.validation.constraints.NotNull;

public class SendSms {

    @NotNull(message = "A Message object must be provided when sending a sms")
    @Property(value = "Message", isObject = true)
    private Sms message;

    public Sms getMessage() {
        return message;
    }
}
