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

    public HashMap<String, String> getManyWhoTwiMLAppConfiguration() {
        HashMap<String,String> urlParms = new HashMap<>();
        urlParms.put("FriendlyName", "ManyWho");
        urlParms.put("SmsUrl", uriInfo.getBaseUri().toString() + "callback/callbackTwiml/message");
        urlParms.put("SmsFallbackUrl", uriInfo.getBaseUri().toString() + "callback/callbackTwiml/message" );
        urlParms.put("SmsStatusCallback", uriInfo.getBaseUri().toString() + "callback/status/message");
        urlParms.put("VoiceUrl", uriInfo.getBaseUri().toString() + "callback/callbackTwiml/voice");
        urlParms.put("VoiceFallbackUrl", uriInfo.getBaseUri().toString() + "callback/callbackTwiml/voice");
        urlParms.put("StatusCallback", uriInfo.getBaseUri().toString() + "callback/status/voice");


        return urlParms;
    }
}
