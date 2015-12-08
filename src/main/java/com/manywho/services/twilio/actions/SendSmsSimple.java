package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import org.hibernate.validator.constraints.NotEmpty;

@Action(name = "Send SMS (Simple)", summary = "Send an SMS message to a phone number without using an Object", uriPart = "messages/smssimple")
public class SendSmsSimple {
    @NotEmpty(message = "The From value must not be null or blank")
    @ActionInput(name = "From", contentType = ContentType.String)
    private String from;

    @NotEmpty(message = "The To value must not be null or blank")
    @ActionInput(name = "To", contentType = ContentType.String)
    private String to;

    @NotEmpty(message = "The Body value must not be null or blank")
    @ActionInput(name = "Body", contentType = ContentType.String)
    private String body;

    @ActionOutput(name = "Reply", contentType = ContentType.String, required = false)
    private String reply;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public String getReply() {
        return reply;
    }
}
