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

    @FormParam("ApiVersion")
    private String apiVersion;

    @FormParam("FromCity")
    private String fromCity;

    @FormParam("FromCountry")
    private String fromCountry;

    @FormParam("FromState")
    private String fromState;

    @FormParam("FromZip")
    private String fromZip;

    @FormParam("NumMedia")
    private String numMedia;

    @FormParam("NumSegments")
    private String NumSegments;

    @FormParam("SmsMessageSid")
    private String smsMessageSid;

    @FormParam("ToCity")
    private String toCity;

    @FormParam("ToCountry")
    private String toCountry;

    @FormParam("ToState")
    private String toState;

    @FormParam("toZip")
    private String toZip;

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

    public String getApiVersion() {
        return apiVersion;
    }

    public String getFromCity() {
        return fromCity;
    }

    public String getFromCountry() {
        return fromCountry;
    }

    public String getFromState() {
        return fromState;
    }

    public String getFromZip() {
        return fromZip;
    }

    public String getNumMedia() {
        return numMedia;
    }

    public String getNumSegments() {
        return NumSegments;
    }

    public String getSmsMessageSid() {
        return smsMessageSid;
    }

    public String getToCity() {
        return toCity;
    }

    public String getToCountry() {
        return toCountry;
    }

    public String getToState() {
        return toState;
    }

    public String getToZip() {
        return toZip;
    }
}
