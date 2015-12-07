package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;

import java.util.List;

@TypeElement(name = Mms.NAME)
public class Mms {
    public final static String NAME = "MMS";

    @TypeProperty(name = "To", contentType = ContentType.String, bound = false)
    private String to;

    @TypeProperty(name = "From", contentType = ContentType.String, bound = false)
    private String from;

    @TypeProperty(name = "Body", contentType = ContentType.String, bound = false)
    private String body;

    @TypeProperty(name = "Media", contentType = ContentType.List, bound = false)
    private List<Media> media;

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    public List<Media> getMedia() {
        return media;
    }
}
