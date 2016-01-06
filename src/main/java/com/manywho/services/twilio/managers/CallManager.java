package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.services.CallService;
import com.twilio.sdk.resource.instance.Call;

import javax.inject.Inject;

public class CallManager {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private CallService callService;

    public void startOutboundCall(ServiceRequest serviceRequest, Configuration configuration, String from, String to, String timeout, String recordCall) throws Exception {
        boolean recordCallBool = Boolean.parseBoolean(recordCall);

        // Begin the outbound call. Twilio will callback to the service to request some TwiML for the call
        Call call = callService.startOutboundCall(
                serviceRequest.getStateId(),
                configuration.getAccountSid(),
                configuration.getAuthToken(),
                from,
                to,
                timeout,
                recordCallBool
        );

        // Cache the call SID for future callbacks to ManyWho
        cacheManager.saveCallRequest(call.getSid(), serviceRequest);
    }
}
