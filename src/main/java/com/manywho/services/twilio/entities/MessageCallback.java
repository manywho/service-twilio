package com.manywho.services.twilio.entities;

import javax.ws.rs.FormParam;

public class MessageCallback {
    @FormParam("AccountSid")
    private String accountSid;

    @FormParam("Body")
    private String body;

    @FormParam("ErrorCode")
    private String errorCode;

    @FormParam("From")
    private String from;

    @FormParam("MessageSid")
    private String messageSid;

    @FormParam("MessageStatus")
    private String messageStatus;

    @FormParam("SmsSid")
    private String smsSid;

    @FormParam("SmsStatus")
    private String smsStatus;

    @FormParam("To")
    private String to;

    public String getAccountSid() {
        return accountSid;
    }

    public String getBody() {
        return body;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getFrom() {
        return from;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public String getSmsSid() {
        return smsSid;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public String getTo() {
        return to;
    }
}
