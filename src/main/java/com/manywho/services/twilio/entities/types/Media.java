package com.manywho.services.twilio.entities.types;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;

@Type(com.manywho.services.twilio.types.Media.NAME)
public class Media {
    @Property("URL")
    private String url;

    public String getUrl() {
        return url;
    }
}
