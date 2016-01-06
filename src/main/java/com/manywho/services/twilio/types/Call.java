package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;

@TypeElement(name = Call.NAME)
public class Call {
    public final static String NAME = "Call";

    @TypeProperty(name = "To", contentType = ContentType.String, bound = false)
    private String to;

    @TypeProperty(name = "From", contentType = ContentType.String, bound = false)
    private String from;

    @TypeProperty(name = "Timeout", contentType = ContentType.Number, bound = false)
    private String timeout;

    @TypeProperty(name = "Record?", contentType = ContentType.Boolean, bound = false)
    private String record;

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getRecord() {
        return record;
    }
}
