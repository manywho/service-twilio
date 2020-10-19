package com.manywho.services.twilio.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.types.Media;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private TwilioConfiguration twilioConfiguration;

    @Inject
    private TwilioRestClientFactory twilioClientFactory;

    public Message sendMms(String accountSid, String authToken, String to, String from, String body, List<Media> medias) throws Exception {
        return sendMessage(accountSid, authToken, to, from, body, medias);
    }

    public Message sendSms(String accountSid, String authToken, String to, String from, String body) throws Exception {
        return sendMessage(accountSid, authToken, to, from, body, new ArrayList<>());
    }

    private Message sendMessage(String accountSid, String authToken, String to, String from, String body, List<Media> medias) throws TwilioRestException {
        final Account account = twilioClientFactory.createTwilioRestClient(accountSid, authToken).getAccount();

        final List<NameValuePair> messageParameters = new ArrayList<>();
        messageParameters.add(new BasicNameValuePair("To", to));
        messageParameters.add(new BasicNameValuePair("From", from));
        messageParameters.add(new BasicNameValuePair("Body", body));
        messageParameters.add(new BasicNameValuePair("StatusCallback", twilioConfiguration.getSmsStatusCallback()));

        for (Media media : medias) {
            messageParameters.add(new BasicNameValuePair("MediaUrl", media.getUrl()));
        }

        LOGGER.debug("Sending a message to {} with {} media items", to, medias.size());

        return account.getMessageFactory().create(messageParameters);
    }

    public void storeMessageRequest(String accountSid, String messageSid, String from, String to, ServiceRequest request) throws Exception {
        String serializedServiceRequest = objectMapper.writeValueAsString(request);

        // Store the Message SID to check if it was delivered
        cacheManager.saveMessageRequest(accountSid, messageSid, serializedServiceRequest);
        // Store the request under the To number, as it's the only way to match an incoming message to a sent one
        cacheManager.saveMessageRequest(accountSid, from + to, serializedServiceRequest);
    }

    public void storeAuthenticatedWho(String accountSid, String messageSid, String from, String to, AuthenticatedWho authenticatedWho) throws Exception {
        String serializedAuthenticatedWho = objectMapper.writeValueAsString(authenticatedWho);
        
        cacheManager.saveAuthenticatedWho(accountSid, messageSid, serializedAuthenticatedWho);
        cacheManager.saveAuthenticatedWho(accountSid, from + to, serializedAuthenticatedWho);
    }
}
