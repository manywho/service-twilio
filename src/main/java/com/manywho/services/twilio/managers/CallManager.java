package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.services.CallService;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLResponse;

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
        TwiMLResponse twiMLResponse = new TwiMLResponse();
        Say say = new Say(message);
        say.setVoice(voice);
        say.setLanguage(language);
        twiMLResponse.append(say);

        callService.startOutboundCall(
            serviceRequest.getStateId(),
            configuration.getAccountSid(),
            configuration.getAuthToken(),
            from,
            to,
            "60",
            false,
            this.uriInfo.getBaseUri().toString() + "callback/twiml/echotwiml?twiml=" + twiMLResponse.asURL()
        );
    }
}
