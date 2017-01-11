package com.manywho.services.twilio.actions;


import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import com.manywho.services.twilio.types.SmsWebhook;
import org.hibernate.validator.constraints.NotEmpty;

@Action(name = "Fetch SMS Webhook", summary = "Fetch data about the SMS that have triggered the webhook", uriPart = "messages/webhook/sms")
public class FetchSmsWebhook {

    @NotEmpty(message = "The Message Sid value must not be null or blank")
    @ActionInput(name = "Message Sid", contentType = ContentType.String)
    private String messageSid;

    @ActionOutput(name = "SMS Webhook", contentType = ContentType.Object, required = false)
    private SmsWebhook smsWebhook;

    public String getMessageSid() {
        return messageSid;
    }

    public SmsWebhook getSmsWebhook() {
        return smsWebhook;
    }
}
