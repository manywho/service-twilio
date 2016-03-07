package com.manywho.services.twilio.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;
import org.hibernate.validator.constraints.NotEmpty;

@TypeElement(name = Recording.NAME)
public class Recording {
    public final static String NAME = "Recording";
    public final static String PROPERTY_TRANSCRIPTION = "Transcription";
    public final static String PROPERTY_URL = "Url";

    @Id
    private String id;

    @TypeProperty(name = PROPERTY_TRANSCRIPTION, contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The Transcription value must not be null or blank")
    private String transcription;

    @TypeProperty(name = PROPERTY_URL, contentType = ContentType.String, bound = false)
    @NotEmpty(message = "The Url value must not be null or blank")
    private String url;

    public String getTranscription() {
        return transcription;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
