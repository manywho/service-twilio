package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import com.manywho.services.twilio.types.Mms;

import javax.validation.constraints.NotNull;

@Action(name = "Send MMS", summary = "Send an MMS message to a phone number", uriPart = "messages/mms")
public class SendMms {
    @NotNull(message = "A Message object must be provided when sending an MMS")
    @ActionInput(name = "Message", contentType = ContentType.Object)
    private Mms message;

    @ActionOutput(name = "Reply", contentType = ContentType.Object, required = false)
    private Mms reply;

    public Mms getMessage() {
        return message;
    }

    public Mms getReply() {
        return reply;
    }
}
