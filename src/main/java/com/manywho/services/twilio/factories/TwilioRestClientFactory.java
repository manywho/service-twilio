package com.manywho.services.twilio.factories;

import com.twilio.sdk.TwilioRestClient;

public class TwilioRestClientFactory {

    public TwilioRestClient createTwilioRestClient(String accountSid, String authToken) {
        return new TwilioRestClient(accountSid, authToken);
    }
}
