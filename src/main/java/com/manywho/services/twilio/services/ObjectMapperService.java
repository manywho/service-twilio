package com.manywho.services.twilio.services;

import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.twilio.types.Media;
import com.manywho.services.twilio.types.Mms;
import com.manywho.services.twilio.types.Recording;
import com.manywho.services.twilio.types.Sms;

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
}
