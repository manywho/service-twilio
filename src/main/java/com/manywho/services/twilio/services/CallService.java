package com.manywho.services.twilio.services;

import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.CallbackTwimlManager;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

public class CallService {

    @Context
    private UriInfo uriInfo;

    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    public Call startOutboundCall(String stateId, String accountSid, String authToken, String from, String to, String timeout, boolean recordCall) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final Map<String, String> callParameters = new HashMap<>();
        callParameters.put("To", to);
        callParameters.put("From", from);
        callParameters.put("Timeout", timeout);
        callParameters.put("IfMachine", "Continue");
        callParameters.put("Record", Boolean.toString(recordCall));
        callParameters.put("StatusCallback", "https://" + uriInfo.getBaseUri().getHost() + "/api/twilio/2/callback/status/voice");
        callParameters.put("Url", "https://" + uriInfo.getBaseUri().getHost() + "/api/twilio/2/callback/twiml/voice/flow/state/" + stateId);

        return account.getCallFactory().create(callParameters);
    }
}
