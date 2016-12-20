package com.manywho.services.twilio.managers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.services.ObjectMapperService;
import com.manywho.services.twilio.types.CallRecording;
import com.manywho.services.twilio.types.SmsWebhook;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class DataManager {

    @Inject
    CacheManager cacheManager;

    @Inject
    ObjectMapperService objectMapperService;

    @Inject
    CallbackManager callbackManager;

    public ObjectCollection loadRecordingForCall(ObjectDataRequest objectDataRequest) {
        String callId = "";

        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            callId = objectDataRequest.getListFilter().getId();
        }

        Optional<EngineValue> engineValueAccountSid = objectDataRequest.getConfigurationValues().stream()
                .filter(engineValue -> Objects.equals(engineValue.getDeveloperName(), "Account SID")).findFirst();

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", callId));
        properties.add(new Property("Recording Url", callbackManager.getCallRecordingUrl(engineValueAccountSid.get().getContentValue(), callId)));

        Object object = new Object();
        object.setDeveloperName(CallRecording.NAME);
        object.setExternalId(callId);
        object.setProperties(properties);

        return new ObjectCollection(object);
    }

    public MObject loadSmsWebhook(String messageSid) {
        if (StringUtils.isNotEmpty(messageSid)) {
            MessageCallback messageCall = cacheManager.getSmsWebhook(messageSid);
            return objectMapperService.convertSmsWebhookToObject(messageCall);
        } else {
            throw new RuntimeException("Filter not supported for sms webhook");
        }
    }
}
