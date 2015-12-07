package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;

@TypeElement(name = Media.NAME)
public class Media {
    public final static String NAME = "Media";

    @TypeProperty(name = "URL", contentType = ContentType.String, bound = false)
    private String url;

    public String getUrl() {
        return url;
    }
}
