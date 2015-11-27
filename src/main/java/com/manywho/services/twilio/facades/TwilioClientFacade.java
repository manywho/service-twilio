package com.manywho.services.twilio.facades;

import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.Application;
import com.twilio.sdk.resource.instance.IncomingPhoneNumber;
import java.util.HashMap;
import java.util.Optional;
import javax.inject.Inject;

public class TwilioClientFacade {

    @Inject
    private TwilioConfiguration twilioConfiguration;

    public IncomingPhoneNumber getNumberInstance(TwilioRestClient twilioRestClient, String fromNumber) throws Exception {
        HashMap<String, String> filter = new HashMap<>();
        filter.put("PhoneNumber", fromNumber);

        Optional<IncomingPhoneNumber> number = StreamUtils.asStream(twilioRestClient.getAccount().getIncomingPhoneNumbers(filter).iterator())
                .findFirst();

        if(number.isPresent()) {
            return number.get();
        }

        throw new Exception("The 'From' phone number doesn't exist in the account");
    }

    public void assignTwiMLAppToPhoneNumber(String applicationSid, IncomingPhoneNumber number) throws TwilioRestException {

        HashMap<String, String> changes = new HashMap<>();
        changes.put("VoiceApplicationSid", applicationSid);
        changes.put("SmsApplicationSid", applicationSid);
        number.update(changes);
    }

    public String getExistingApplicationFromTwilio(TwilioRestClient twilioRestClient) {
        HashMap<String, String> urlParams = twilioConfiguration.getManyWhoTwiMLAppConfiguration();

        Optional<Application> existingApplication = StreamUtils.asStream(twilioRestClient.getAccount().getApplications(urlParams).iterator())
                .findFirst();

        if(existingApplication.isPresent()) {
            return existingApplication.get().getSid();
        }

        return null;
    }

    public Application createManyWhoTwiMLApplication(TwilioRestClient twilioRestClient ) throws TwilioRestException {
        return twilioRestClient
                .getAccount()
                .getApplicationFactory()
                .create(twilioConfiguration.getManyWhoTwiMLAppConfiguration());
    }
}
