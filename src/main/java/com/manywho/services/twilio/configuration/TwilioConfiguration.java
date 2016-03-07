package com.manywho.services.twilio.configuration;

import com.manywho.sdk.services.config.ServiceConfiguration;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;

public class TwilioConfiguration {

    @Inject
    private ServiceConfiguration configuration;

    @Context
    private UriInfo uriInfo;

    public String getFriendlyName() {
        return "ManyWho";
    }

    public String getSmsUrl() {
        return uriInfo.getBaseUri().toString() + "callback/callbackTwiml/message";
    }

    public String getSmsFallbackUrl() {
        return uriInfo.getBaseUri().toString() + "callback/callbackTwiml/message" ;
    }

    public String getSmsStatusCallback() {
        return uriInfo.getBaseUri().toString() + "callback/status/message";
    }

    public String getVoiceUrl() {
        return uriInfo.getBaseUri().toString() + "callback/callbackTwiml/voice";
    }

    public String getVoiceFallbackUrl() {
        return uriInfo.getBaseUri().toString() + "callback/callbackTwiml/voice";
    }

    public String getStatusCallback() {
        return uriInfo.getBaseUri().toString() + "callback/status/voice";
    }

    public String getCallbackTwimlVoiceFlowState(){
        return uriInfo.getBaseUri().toString() + "callback/callbackTwiml/voice/flow/state/";
    }

    public String getCallbackTranscription() {
        return uriInfo.getBaseUri().toString() + "callback/transcribe/";
    }

}
