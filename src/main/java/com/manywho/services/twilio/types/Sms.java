package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;
import org.hibernate.validator.constraints.NotEmpty;

@TypeElement(name = Sms.NAME)
public class Sms {
    public final static String NAME = "SMS";

    @Id
    private String id;

    @TypeProperty(name = "To", contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The To value must not be null or blank")
    private String to;

    @TypeProperty(name = "From", contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The From value must not be null or blank")
    private String from;

    @TypeProperty(name = "Body", contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The Body value must not be null or blank")
    private String body;

    public Sms() {
    }

    public Sms(String id, String to, String from, String body) {
        this.id = id;
        this.to = to;
        this.from = from;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }
}
