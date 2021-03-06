package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.services.MessageService;
import com.manywho.services.twilio.services.ObjectMapperService;
import com.manywho.services.twilio.types.Media;
import com.manywho.services.twilio.types.Mms;
import com.twilio.sdk.resource.instance.Message;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    @Inject
    private MessageService messageService;

    @Inject
    private ObjectMapperService objectMapperService;

    public ObjectCollection sendMms(ServiceRequest serviceRequest, Configuration configuration, AuthenticatedWho authenticatedWho, String to, String from, String body, List<Media> media) throws Exception {
        // Send the MMS through Twilio
        final Message mms = messageService.sendMms(configuration.getAccountSid(), configuration.getAuthToken(), to, from, body, media);

        // Store the message request in Redis for later
        messageService.storeMessageRequest(mms.getAccountSid(), mms.getSid(), mms.getFrom(), mms.getTo(), serviceRequest);

        // Store the authenticatedWho for the request in Redis for any callbacks later
        messageService.storeAuthenticatedWho(mms.getAccountSid(), mms.getSid(), mms.getFrom(), mms.getTo(), authenticatedWho);

        return new ObjectCollection(objectMapperService.convertMmsToObject(new Mms(
                mms.getSid(),
                "",
                "",
                "",
                new ArrayList<>()
        )));
    }

    public ObjectCollection sendSms(ServiceRequest serviceRequest, Configuration configuration, AuthenticatedWho authenticatedWho, String to, String from, String body) throws Exception {
         // Send the SMS through Twilio
        final Message sms = messageService.sendSms(configuration.getAccountSid(), configuration.getAuthToken(), to, from, body);

        // Store the message request in Redis for later
        messageService.storeMessageRequest(sms.getAccountSid(), sms.getSid(), sms.getFrom(), sms.getTo(), serviceRequest);

        // Store the authenticatedWho for the request in Redis for any callbacks later
        messageService.storeAuthenticatedWho(sms.getAccountSid(), sms.getSid(), sms.getFrom(), sms.getTo(), authenticatedWho);

        return new ObjectCollection(objectMapperService.convertSmsToObject(new com.manywho.services.twilio.types.Sms(
                sms.getSid(),
                "",
                "",
                ""
        )));
    }
}
