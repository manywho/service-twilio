package com.manywho.services.twilio.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.entities.types.Media;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.managers.TwimlApplicationManager;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.instance.Sms;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {

    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Context
    private UriInfo uriInfo;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private TwimlApplicationManager twimlApplication;

    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    public Sms sendSms(String accountSid, String authToken, String to, String from, String body) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final Map<String, String> messageParameters = new HashMap<>();
        messageParameters.put("To", to);
        messageParameters.put("From", from);
        messageParameters.put("Body", body);
        messageParameters.put("ApplicationSid", twimlApplication.getApplicationSid(accountSid, authToken, from));

        LOGGER.debug("Sending an SMS to {}", to);

        return account.getSmsFactory().create(messageParameters);
    }

    public Message sendMms(String accountSid, String authToken, String to, String from, String body, List<Media> medias) throws Exception {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final List<NameValuePair> messageParameters = new ArrayList<>();
        messageParameters.add(new BasicNameValuePair("To", to));
        messageParameters.add(new BasicNameValuePair("From", from));
        messageParameters.add(new BasicNameValuePair("Body", body));
        messageParameters.add(new BasicNameValuePair("ApplicationSid", twimlApplication.getApplicationSid(accountSid, authToken, from)));

        for (Media media : medias) {
            messageParameters.add(new BasicNameValuePair("MediaUrl", media.getUrl()));
        }

        LOGGER.debug("Sending an MMS to {} with {} media items", to, medias.size());

        return account.getMessageFactory().create(messageParameters);
    }

    public void storeMessageRequest(String accountSid, String messageSid, String from, String to, ServiceRequest request) throws Exception {
        String serializedServiceRequest = objectMapper.writeValueAsString(request);

        // Store the Message SID to check if it was delivered
        cacheManager.saveMessageRequest(accountSid, messageSid, serializedServiceRequest);
        // Store the request under the To number, as it's the only way to match an incoming message to a sent one
        cacheManager.saveMessageRequest(accountSid, from + to, serializedServiceRequest);
    }
}
