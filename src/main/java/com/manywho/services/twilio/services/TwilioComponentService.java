package com.manywho.services.twilio.services;

import com.manywho.sdk.entities.run.elements.ui.PageComponentDataResponse;
import com.manywho.sdk.entities.run.elements.ui.PageComponentResponse;
import com.manywho.sdk.entities.run.elements.ui.PageResponse;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Pause;
import com.twilio.sdk.verbs.Play;
import com.twilio.sdk.verbs.Record;
import com.twilio.sdk.verbs.Redirect;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.Verb;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.util.Optional;

public class TwilioComponentService {
    public Verb createTwimlForComponent(PageResponse pageResponse, PageComponentResponse component, String stateId) {
        Optional<PageComponentDataResponse> componentDataResponse = findComponentData(pageResponse, component);

        if (componentDataResponse.isPresent()) {
            PageComponentDataResponse componentData = componentDataResponse.get();

            switch (component.getComponentType()) {
                case "Gather":
                    return createGatherComponent(component);
                case "Play":
                    return createPlayComponent(component, componentData);
                case "PRESENTATION":
                    return createSayPresentationComponent(component, componentData);
                case "Record":
                    return createRecordComponent(component, stateId);
                case "Say":
                    return createSayComponent(component, componentData);
            }
        }

        return null;
    }

    private static Record createRecordComponent(PageComponentResponse component, String stateId) {
        Record record = new Record();

        record.setAction(component.getAttributes().get("action"));

        String timeout = component.getAttributes().get("timeout");
        if (StringUtils.isNotEmpty(timeout)) {
            record.setTimeout(Integer.parseInt(timeout));
        }

        record.setMethod(component.getAttributes().get("method"));
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
        if (Boolean.parseBoolean(transcribe) & StringUtils.isEmpty(transcriptionCallback)) {
            record.setTranscribeCallback("https://manywhoservices.ngrok.com/api/twilio/2/callback/transcribe/" + stateId);
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

    static Gather createGatherComponent(PageComponentResponse component) {
        Gather gather = new Gather();
        gather.setAction(component.getAttributes().get("action"));
        gather.setFinishOnKey(component.getAttributes().get("finishOnKey"));
        gather.setMethod(component.getAttributes().get("method"));

        String numDigits = component.getAttributes().get("action");
        if (StringUtils.isNotEmpty(numDigits)) {
            gather.setNumDigits(Integer.parseInt(numDigits));
        }

        String timeout = component.getAttributes().get("action");
        if (StringUtils.isNotEmpty(timeout)) {
            gather.setTimeout(Integer.parseInt(timeout));
        }

        return gather;
    }

    static Play createPlayComponent(PageComponentResponse component, PageComponentDataResponse componentData) {
        Play play = new Play(componentData.getContent());

        String loop = component.getAttributes().get("loop");
        if (StringUtils.isNotEmpty(loop)) {
            play.setLoop(Integer.parseInt(loop));
        }

        return play;
    }

    static Say createSayPresentationComponent(PageComponentResponse component, PageComponentDataResponse componentData) {
        String plainText = Jsoup.parse(componentData.getContent()).text();

        return new Say(plainText);
    }

    static Say createSayComponent(PageComponentResponse component, PageComponentDataResponse componentData) {
        Say say = new Say(componentData.getContent());
        say.setLanguage(component.getAttributes().get("language"));
        say.setVoice(component.getAttributes().get("voice"));

        String loop = component.getAttributes().get("loop");
        if (StringUtils.isEmpty(loop)) {
            loop = "1";
        }

        say.setLoop(Integer.parseInt(loop));

        return say;
    }

    static Optional<PageComponentDataResponse> findComponentData(PageResponse pageResponse, PageComponentResponse component) {
        return pageResponse.getPageComponentDataResponses().stream()
                .filter(componentData -> componentData.getPageComponentId().equals(component.getId()))
                .findFirst();
    }
}
