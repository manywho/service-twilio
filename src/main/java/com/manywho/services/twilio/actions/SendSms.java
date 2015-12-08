package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import com.manywho.services.twilio.types.Sms;

import javax.validation.constraints.NotNull;

@Action(name = "Send SMS", summary = "Send an SMS message to a phone number", uriPart = "messages/sms")
public class SendSms {
    @NotNull(message = "A Message object must be provided when sending an SMS")
    @ActionInput(name = "Message", contentType = ContentType.Object)
    private Sms message;

    @ActionOutput(name = "Reply", contentType = ContentType.Object, required = false)
    private Sms reply;

    public Sms getMessage() {
        return message;
    }

    public Sms getReply() {
        return reply;
    }
}
