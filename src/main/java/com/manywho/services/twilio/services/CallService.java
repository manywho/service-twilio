package com.manywho.services.twilio.services;

import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.CallbackManager;
import com.manywho.services.twilio.managers.TwimlApplicationManager;
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
    private TwimlApplicationManager twiMLApplication;

    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    public Call startOutboundCall(String stateId, String accountSid, String authToken, String from, String to, int timeout, boolean recordCall) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final Map<String, String> callParameters = new HashMap<String, String>() {{
            put("To", to);
            put("From", from);
            put("Timeout", String.valueOf(timeout));
            put("IfMachine", "Continue");
            put("Record", Boolean.toString(recordCall));
            put("StatusCallback", CallbackManager.BASE_CALLBACK_LOCATION + "/callback/status/voice");
            put("Url", CallbackManager.BASE_CALLBACK_LOCATION + "/api/twilio/2/callback/twiml/voice/flow/state/" + stateId);
        }};

        return account.getCallFactory().create(callParameters);
    }
}
