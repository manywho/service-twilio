package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import org.hibernate.validator.constraints.NotEmpty;

@Action(name = "Voice (Simple)", summary = "Call a number and use text to speech to vocalize a message", uriPart = "calls/voicesimple")
public class VoiceSimple {
    @NotEmpty(message = "The From value must not be null or blank")
    @ActionInput(name = "From", contentType = ContentType.String)
    private String from;

    @NotEmpty(message = "The To value must not be null or blank")
    @ActionInput(name = "To", contentType = ContentType.String)
    private String to;

    @NotEmpty(message = "The Message value must not be null or blank")
    @ActionInput(name = "Message", contentType = ContentType.String)
    private String message;

    @ActionInput(name = "Language", contentType = ContentType.String, required = false)
    private String language;

    @ActionInput(name = "Voice", contentType = ContentType.String, required = false)
    private String voice;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() { return message; }

    public String getVoice() { return voice; }

    public String getLanguage() { return language; }


}
