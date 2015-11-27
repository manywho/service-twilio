package com.manywho.services.twilio.services;

import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.TwimlApplicationManager;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;
import org.apache.commons.lang3.StringUtils;

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

    public Call startOutboundCall(String accountSid, String authToken, String from, String to, String timeout) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final Map<String, String> callParameters = new HashMap<String, String>() {{
            put("To", to);
            put("From", from);
            put("Timeout", StringUtils.isNotEmpty(timeout) ? timeout : "60");
            put("IfMachine", "Continue");
            put("ApplicationSid", twiMLApplication.getApplicationSid(accountSid, authToken, from));
        }};

        return account.getCallFactory().create(callParameters);
    }
}
