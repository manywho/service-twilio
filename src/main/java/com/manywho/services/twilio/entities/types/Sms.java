package com.manywho.services.twilio.entities.types;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Type(com.manywho.services.twilio.types.Sms.NAME)
public class Sms {

    @NotEmpty(message = "The From value must not be null or blank")
    @Property("From")
    private String from;

    @NotEmpty(message = "The To value must not be null or blank")
    @Property("To")
    private String to;

    @NotEmpty(message = "The Body value must not be null or blank")
    @Property("Body")
    private String body;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }
}
