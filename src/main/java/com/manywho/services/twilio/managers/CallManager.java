package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.services.CallService;
import com.twilio.sdk.resource.instance.Call;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URLEncoder;

public class CallManager {

    @Context
    private UriInfo uriInfo;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private CallService callService;

    public String startOutboundCall(ServiceRequest serviceRequest, Configuration configuration, String from, String to, String timeout, String recordCall) throws Exception {
        boolean recordCallBool = Boolean.parseBoolean(recordCall);

        // Begin the outbound call. Twilio will callback to the service to request some TwiML for the call
        Call call = callService.startOutboundCall(
                serviceRequest.getStateId(),
                configuration.getAccountSid(),
                configuration.getAuthToken(),
                from,
                to,
                timeout,
                recordCallBool,
                null
        );

        // Cache the call SID for future callbacks to ManyWho
        cacheManager.saveCallRequest(call.getSid(), serviceRequest);

        return call.getSid();
    }

    public void voiceMessage(ServiceRequest serviceRequest, Configuration configuration, String from,  String to, String message, String voice, String language) throws Exception {

        String twiml = "%3CSay%20voice%3D%22" + voice + "%22%20language%3D%22" + language + "%22%3E" + URLEncoder.encode(message) + "%3C%2FSay%3E";

        callService.startOutboundCall(
            serviceRequest.getStateId(),
            configuration.getAccountSid(),
            configuration.getAuthToken(),
            from,
            to,
            "60",
            false,
            this.uriInfo.getBaseUri().toString() + "callback/callbackTwiml/echotwiml?twiml=" + twiml
        );
    }
}
