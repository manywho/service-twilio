package com.manywho.services.twilio.managers;

import com.manywho.services.twilio.facades.TwilioClientFacade;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.twilio.sdk.TwilioRestClient;
import javax.inject.Inject;

public class TwimlApplicationManager {
    @Inject
    private CacheManager cacheManager;

    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    @Inject
    private TwilioClientFacade twilioClientFacade;

    public String getApplicationSid(String accountSid, String authToken, String fromNumber) throws Exception {

        String existingApplicationSid = cacheManager.getTwimlApplication(accountSid);

        if (existingApplicationSid != null) {
            return existingApplicationSid;
        }

        TwilioRestClient twilioRestClient = twilioClientFactory.createTwilioRestClient(accountSid, authToken);
        String applicationSid = twilioClientFacade.getExistingApplicationFromTwilio(twilioRestClient);

        if (applicationSid == null) {
            applicationSid = twilioClientFacade.createManyWhoTwiMLApplication(twilioRestClient).getSid();

            twilioClientFacade.assignTwiMLAppToPhoneNumber(
                    applicationSid,
                    twilioClientFacade.getNumberInstance(twilioRestClient, fromNumber)
            );
        }

        cacheManager.setTwimlApplication(accountSid, applicationSid);

        return applicationSid;
    }
}
