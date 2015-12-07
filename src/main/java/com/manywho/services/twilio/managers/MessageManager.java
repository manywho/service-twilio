package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.services.MessageService;
import com.manywho.services.twilio.types.Media;
import com.manywho.services.twilio.types.Mms;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.instance.Sms;

import javax.inject.Inject;
import java.util.List;

public class MessageManager {

    @Inject
    private MessageService messageService;

    public ObjectCollection sendMms(ServiceRequest serviceRequest, Configuration configuration, String to, String from, String body, List<Media> media) throws Exception {
        // Send the MMS through Twilio
        final Message mms = messageService.sendMms(configuration.getAccountSid(), configuration.getAuthToken(), to, from, body, media);

        // Store the message request in Redis for later
        messageService.storeMessageRequest(mms.getAccountSid(), mms.getSid(), mms.getFrom(), mms.getTo(), serviceRequest);

        return new ObjectCollection() {{
            add(new Object() {{
                setDeveloperName(Mms.NAME);
                setExternalId(mms.getSid());
                setProperties(new PropertyCollection() {{
                    add(new Property("To", ""));
                    add(new Property("From", ""));
                    add(new Property("Body", ""));
                    add(new Property("Media", ""));
                }});
            }});
        }};
    }

    public ObjectCollection sendSms(ServiceRequest serviceRequest, Configuration configuration, String to, String from, String body) throws Exception {
         // Send the SMS through Twilio
        final Sms sms = messageService.sendSms(configuration.getAccountSid(), configuration.getAuthToken(), to, from, body);

        // Store the message request in Redis for later
        messageService.storeMessageRequest(sms.getAccountSid(), sms.getSid(), sms.getFrom(), sms.getTo(), serviceRequest);

        return new ObjectCollection() {{
            add(new Object() {{
                setDeveloperName(com.manywho.services.twilio.types.Sms.NAME);
                setExternalId(sms.getSid());
                setProperties(new PropertyCollection() {{
                    add(new Property("To", ""));
                    add(new Property("From", ""));
                    add(new Property("Body", ""));
                }});
            }});
        }};
    }
}
