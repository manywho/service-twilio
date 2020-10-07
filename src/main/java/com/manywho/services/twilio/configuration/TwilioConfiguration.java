package com.manywho.services.twilio.configuration;

import com.manywho.sdk.services.config.ServiceConfiguration;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class TwilioConfiguration {

    @Inject
    private ServiceConfiguration configuration;

    @Context
    private UriInfo uriInfo;

    public String getFriendlyName() {
        return "ManyWho";
    }

    public String getSmsUrl() {
        return uriInfo.getBaseUri().toString() + "callback/twiml/message";
    }

    public String getSmsFallbackUrl() {
        return uriInfo.getBaseUri().toString() + "callback/twiml/message";
    }

    public String getSmsStatusCallback() {
        return "http://04d618b2f04a.ngrok.io/" + "callback/status/message";
    }

    public String getVoiceUrl() {
        return uriInfo.getBaseUri().toString() + "callback/twiml/voice";
    }

    public String getVoiceFallbackUrl() {
        return uriInfo.getBaseUri().toString() + "callback/twiml/voice";
    }

    public String getStatusCallback() {
        return uriInfo.getBaseUri().toString() + "callback/status/voice";
    }

    public String getCallbackTwimlVoiceFlowState(){
        return uriInfo.getBaseUri().toString() + "callback/twiml/voice/flow/state/";
    }

    public String getCallbackTwimlSmsFlowState(){
        return uriInfo.getBaseUri().toString() + "callback/twiml/sms/flow/state/";
    }

    public String getCallbackTranscription() {
        return uriInfo.getBaseUri().toString() + "callback/transcribe/";
    }

}
