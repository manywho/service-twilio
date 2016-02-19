package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import org.hibernate.validator.constraints.NotEmpty;

@Action(name = "Start Outbound Call (Simple)", summary = "Start an outbound phone call without using an Object", uriPart = "calls/outboundsimple")
public class StartOutboundCallSimple {
    @NotEmpty(message = "The From value must not be null or blank")
    @ActionInput(name = "From", contentType = ContentType.String)
    private String from;

    @NotEmpty(message = "The To value must not be null or blank")
    @ActionInput(name = "To", contentType = ContentType.String)
    private String to;

    @ActionInput(name = "Timeout", contentType = ContentType.Number)
    private String timeout;

    @ActionInput(name = "Record?", contentType = ContentType.Boolean)
    private String record;

    @ActionOutput(name = "Call Sid", contentType = ContentType.String)
    private String callSid;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getRecord() {
        return record;
    }
}
