package com.manywho.services.twilio.entities.types;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;

@Type(com.manywho.services.twilio.types.Sms.NAME)
public class Sms {

    @Property("From")
    private String from;

    @Property("To")
    private String to;

    @Property("Body")
    private String body;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }
}
