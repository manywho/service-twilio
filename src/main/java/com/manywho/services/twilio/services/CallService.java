package com.manywho.services.twilio.services;

import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CallService {


    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    @Inject
    private TwilioConfiguration twilioConfiguration;

    public Call startOutboundCall(String stateId, String accountSid, String authToken, String from, String to, String timeout, boolean recordCall) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final Map<String, String> callParameters = new HashMap<>();
        callParameters.put("To", to);
        callParameters.put("From", from);
        callParameters.put("Timeout", timeout);
        callParameters.put("IfMachine", "Hangup");
        callParameters.put("Record", Boolean.toString(recordCall));
        callParameters.put("StatusCallback", twilioConfiguration.getManyWhoTwiMLAppConfiguration().get("StatusCallback"));
        callParameters.put("Url", twilioConfiguration.getManyWhoTwiMLAppConfiguration().get("CallbackTwimlVoiceFlowState") + stateId);

        return account.getCallFactory().create(callParameters);
    }
}
