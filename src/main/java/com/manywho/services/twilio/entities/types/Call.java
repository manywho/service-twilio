package com.manywho.services.twilio.entities.types;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

@Type(com.manywho.services.twilio.types.Call.NAME)
public class Call {

    @Property("From")
    private String from;

    @Property("To")
    private String to;

    @Property("Timeout")
    private String timeout;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTimeout() {
        return timeout;
    }
}
