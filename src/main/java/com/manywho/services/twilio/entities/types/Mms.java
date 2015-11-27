package com.manywho.services.twilio.entities.types;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;

import java.util.List;

@Type(com.manywho.services.twilio.types.Mms.NAME)
public class Mms {

    @Property("From")
    private String from;

    @Property("To")
    private String to;

    @Property("Body")
    private String body;

    @Property(value = "Media", isList = true)
    private List<Media> media;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<Media> getMedia() {
        return media;
    }

    public String getBody() {
        return body;
    }
}
