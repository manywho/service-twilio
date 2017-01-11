package com.manywho.services.twilio.services;

import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.types.*;

import java.util.List;
import java.util.stream.Collectors;

// TODO: Extract these into TypeBuilder in the SDK
public class ObjectMapperService {
    public MObject convertMediaToObject(Media media) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("URL", media.getUrl()));

        return new MObject(Media.NAME, null, properties);
    }

    public ObjectCollection convertMediaToObjectCollection(List<Media> medias) {
        return medias.stream()
                .map(this::convertMediaToObject)
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public MObject convertMmsToObject(Mms mms) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("To", mms.getTo()));
        properties.add(new Property("From", mms.getFrom()));
        properties.add(new Property("Body", mms.getBody()));
        properties.add(new Property("Media", convertMediaToObjectCollection(mms.getMedia())));

        return new MObject(Mms.NAME, mms.getId(), properties);
    }

    public MObject convertSmsToObject(Sms sms) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("To", sms.getTo()));
        properties.add(new Property("From", sms.getFrom()));
        properties.add(new Property("Body", sms.getBody()));

        return new MObject(Sms.NAME, sms.getId(), properties);
    }

    public MObject convertRecordingToObject(Recording recording) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property(Recording.PROPERTY_URL, recording.getUrl()));
        properties.add(new Property(Recording.PROPERTY_TRANSCRIPTION, recording.getTranscription()));

        return new MObject(Recording.NAME, recording.getId(), properties);
    }

    public MObject convertSmsWebhookToObject(MessageCallback smsWebhook) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("To", smsWebhook.getTo()));
        properties.add(new Property("Account Sid", smsWebhook.getAccountSid()));
        properties.add(new Property("Api Version", smsWebhook.getApiVersion()));
        properties.add(new Property("Body", smsWebhook.getBody()));
        properties.add(new Property("From", smsWebhook.getFrom()));
        properties.add(new Property("From City", smsWebhook.getFromCity()));
        properties.add(new Property("From Country", smsWebhook.getFromCountry()));
        properties.add(new Property("From State", smsWebhook.getFromState()));
        properties.add(new Property("From Zip", smsWebhook.getFromZip()));
        properties.add(new Property("Message Sid", smsWebhook.getMessageSid()));
        properties.add(new Property("Num Media", smsWebhook.getNumMedia()));
        properties.add(new Property("Num Segments", smsWebhook.getNumSegments()));
        properties.add(new Property("Sms Message Sid", smsWebhook.getMessageSid()));
        properties.add(new Property("Sms Sid", smsWebhook.getSmsSid()));
        properties.add(new Property("Sms Status", smsWebhook.getSmsStatus()));
        properties.add(new Property("To", smsWebhook.getTo()));
        properties.add(new Property("To City", smsWebhook.getToCity()));
        properties.add(new Property("To Country", smsWebhook.getToCountry()));
        properties.add(new Property("To State", smsWebhook.getToState()));
        properties.add(new Property("To Zip", smsWebhook.getToZip()));

        return new MObject(SmsWebhook.NAME, smsWebhook.getMessageSid(), properties);
    }
}
