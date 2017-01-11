package com.manywho.services.twilio.services.twiml;

import com.manywho.sdk.client.entities.PageComponent;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequest;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.entities.verbs.Unsupported;
import com.manywho.services.twilio.services.ObjectMapperService;
import com.twilio.sdk.verbs.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import javax.inject.Inject;

public class TwilioComponentService {
    final private TwilioConfiguration twilioConfiguration;
    final private ObjectMapperService objectMapperService;
    final private TranscriptionService transcriptionService;

    public enum CallbackType {
        SMS_CALLBACK, PHONE_CALL_CALLBACK
    }

    @Inject
    public TwilioComponentService(TwilioConfiguration twilioConfiguration, ObjectMapperService objectMapperService,
                                  TranscriptionService transcriptionService) {
        this.twilioConfiguration = twilioConfiguration;
        this.objectMapperService = objectMapperService;
        this.transcriptionService = transcriptionService;
    }

    public Verb createTwimlForComponent(PageComponent component, String stateId, CallbackType callbackType) {
        switch (component.getType()) {
            case "Play":
                return createPlayComponent(component);
            case "PRESENTATION":
                if (callbackType == CallbackType.PHONE_CALL_CALLBACK) {
                    return createSayPresentationComponent(component);
                } else {
                    return createMessagePresentationComponent(component);
                }
            case "Record":
                return createRecordComponent(component, stateId);
            case "Say":
                return createSayComponent(component);
            default:
                return createUnsupportedComponent();
        }
    }

    private Unsupported createUnsupportedComponent() {
        return new Unsupported();
    }

    private Record createRecordComponent(PageComponent component, String stateId) {
        Record record = new Record();

        record.setAction(component.getAttributes().get("action"));

        String timeout = component.getAttributes().get("timeout");
        if (StringUtils.isNotEmpty(timeout)) {
            record.setTimeout(Integer.parseInt(timeout));
        }

        record.setMethod(component.getAttributes().get("method"));

        //todo Fix in frontEnd
        record.setFinishOnKey(component.getAttributes().get("finishOnKey"));

        String playBeep = component.getAttributes().get("playBeep");
        if (StringUtils.isNotEmpty(playBeep)) {
            record.setPlayBeep(Boolean.parseBoolean(playBeep));
        }

        String maxLength = component.getAttributes().get("maxLength");
        if (StringUtils.isNotEmpty(maxLength)) {
            record.setMaxLength(Integer.parseInt(maxLength));
        }

        String transcribe = component.getAttributes().get("transcribe");
        if (StringUtils.isNotEmpty(transcribe)) {
            record.setTranscribe(Boolean.parseBoolean(transcribe));
        }

        String transcriptionCallback = component.getAttributes().get("transcribeCallback");
        if (Boolean.parseBoolean(transcribe) && StringUtils.isEmpty(transcriptionCallback)) {
            record.setTranscribeCallback(twilioConfiguration.getCallbackTranscription() + stateId);
        } else {
            record.setTranscribeCallback(transcriptionCallback);
        }

        return record;
    }

    public Pause createPauseComponent(int length) {
        Pause pause = new Pause();
        pause.setLength(length);

        return pause;
    }

    public Redirect createRedirectComponent(String url) {
        return new Redirect(url);
    }

    static Play createPlayComponent(PageComponent component) {
        Play play = new Play(component.getData().getContent());

        String loop = component.getAttributes().get("loop");
        if (StringUtils.isNotEmpty(loop)) {
            play.setLoop(Integer.parseInt(loop));
        }

        return play;
    }

    static Say createSayPresentationComponent(PageComponent component) {
        String plainText = Jsoup.parse(component.getData().getContent()).text();

        return new Say(plainText);
    }

    static Message createMessagePresentationComponent(PageComponent component) {
        String plainText = Jsoup.parse(component.getData().getContent()).text();

        return new Message(plainText);
    }


    static Say createSayComponent(PageComponent component) {
        Say say = new Say(component.getData().getContent());
        say.setLanguage(component.getAttributes().get("language"));
        say.setVoice(component.getAttributes().get("voice"));

        String loop = component.getAttributes().get("loop");
        if (StringUtils.isEmpty(loop)) {
            loop = "1";
        }

        say.setLoop(Integer.parseInt(loop));

        return say;
    }

    public Boolean isTranscriptionSelected(PageComponent pageComponent) {
        return pageComponent.getAttributes().containsKey("transcribe") &&
        Boolean.parseBoolean(pageComponent.getAttributes().get("transcribe"));
    }

    public PageComponentInputResponseRequest getInputResponseRequestRecording(PageComponent pageComponent,
                                                                              String recordingUrl, RecordingCallback recordingCallback) {

        PageComponentInputResponseRequest inputResponseRequest = new PageComponentInputResponseRequest();
        inputResponseRequest.setPageComponentId(pageComponent.getId());
        inputResponseRequest.setObjectData(
                new ObjectCollection(
                        objectMapperService.convertRecordingToObject(
                            transcriptionService.getRecording(recordingUrl, recordingCallback)
                        )
                )
        );

        return inputResponseRequest;
    }
}
