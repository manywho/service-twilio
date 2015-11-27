package com.manywho.services.twilio.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.twilio.entities.types.Mms;
import javax.validation.constraints.NotNull;

public class SendMms {
    @NotNull(message = "A Message object must be provided when sending a mms")
    @Property(value = "Message", isObject = true)
    private Mms message;

    public Mms getMessage() {
        return message;
    }
}
