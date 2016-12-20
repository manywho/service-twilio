package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;
import org.hibernate.validator.constraints.NotEmpty;

@TypeElement(name = SmsWebhook.NAME)
public class SmsWebhook {
    public final static String NAME = "SMS Webhook";

    @Id
    private String id;

    @TypeProperty(name = "Account Sid", contentType = ContentType.String, bound = false)
    private String accountSid;

    @TypeProperty(name = "Api Version", contentType = ContentType.String, bound = false)
    private String apiVersion;

    @TypeProperty(name = "Body", contentType = ContentType.String, bound = false)
    private String body;

    @TypeProperty(name = "From", contentType = ContentType.String, bound = false)
    private String from;

    @TypeProperty(name = "From City", contentType = ContentType.String, bound = false)
    private String fromCity;

    @TypeProperty(name = "From Country", contentType = ContentType.String, bound = false)
    private String fromCountry;

    @TypeProperty(name = "From State", contentType = ContentType.String, bound = false)
    private String fromState;

    @TypeProperty(name = "From Zip", contentType = ContentType.String, bound = false)
    private String fromZip;

    @TypeProperty(name = "Message Sid", contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The Message Sid")
    private String messageSid;

    @TypeProperty(name = "Num Media", contentType = ContentType.String, bound = false)
    private String numMedia;

    @TypeProperty(name = "Num Segments", contentType = ContentType.String, bound = false)
    private String numSegments;

    @TypeProperty(name = "Sms Message Sid", contentType = ContentType.String, bound = false)
    private String smsMessageSid;

    @TypeProperty(name = "Sms Sid", contentType = ContentType.String, bound = false)
    private String smsSid;

    @TypeProperty(name = "Sms Status", contentType = ContentType.String, bound = false)
    private String smsStatus;

    @TypeProperty(name = "To", contentType = ContentType.String, bound = false)
    private String to;

    @TypeProperty(name = "To City", contentType = ContentType.String, bound = false)
    private String toCity;

    @TypeProperty(name = "To Country", contentType = ContentType.String, bound = false)
    private String toCountry;

    @TypeProperty(name = "To State", contentType = ContentType.String, bound = false)
    private String toState;

    @TypeProperty(name = "To Zip", contentType = ContentType.String, bound = false)
    private String toZip;


    public SmsWebhook() {}

    public SmsWebhook(String accountSid, String apiVersion, String body, String from, String fromCity, String fromCountry, String fromState, String fromZip, String messageSid, String numMedia, String numSegments, String smsMessageSid, String smsSid, String smsStatus, String to, String toCity, String toCountry, String toState, String toZip) {
        this.id = messageSid;
        this.accountSid = accountSid;
        this.apiVersion = apiVersion;
        this.body = body;
        this.from = from;
        this.fromCity = fromCity;
        this.fromCountry = fromCountry;
        this.fromState = fromState;
        this.fromZip = fromZip;
        this.messageSid = messageSid;
        this.numMedia = numMedia;
        this.numSegments = numSegments;
        this.smsMessageSid = smsMessageSid;
        this.smsSid = smsSid;
        this.smsStatus = smsStatus;
        this.to = to;
        this.toCity = toCity;
        this.toCountry = toCountry;
        this.toState = toState;
        this.toZip = toZip;
    }

    public String getId() {
        return id;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
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

    public String getMessageSid() {
        return messageSid;
    }

    public String getNumMedia() {
        return numMedia;
    }

    public String getNumSegments() {
        return numSegments;
    }

    public String getSmsMessageSid() {
        return smsMessageSid;
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
