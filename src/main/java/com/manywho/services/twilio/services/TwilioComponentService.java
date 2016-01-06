package com.manywho.services.twilio.services;

import com.manywho.sdk.client.entities.PageComponent;
import com.manywho.sdk.entities.run.elements.ui.PageContainerResponse;
import com.manywho.services.twilio.entities.verbs.Dummy;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Pause;
import com.twilio.sdk.verbs.Play;
import com.twilio.sdk.verbs.Record;
import com.twilio.sdk.verbs.Redirect;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.Verb;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class TwilioComponentService {
    @Context
    private UriInfo uriInfo;

    public Verb createTwimlForComponent(PageComponent component, String stateId) {
        switch (component.getType()) {
            case "Play":
                return createPlayComponent(component);
            case "PRESENTATION":
                return createSayPresentationComponent(component);
            case "Record":
                return createRecordComponent(component, stateId);
            case "Say":
                return createSayComponent(component);
            default:
                return createDummyComponent();
        }
    }

    private Dummy createDummyComponent() {
        return new Dummy();
    }

    private Record createRecordComponent(PageComponent component, String stateId) {
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
        if (Boolean.parseBoolean(transcribe) && StringUtils.isEmpty(transcriptionCallback)) {
            record.setTranscribeCallback("https://" + uriInfo.getBaseUri().getHost() + "/api/twilio/2/callback/transcribe/" + stateId);
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
}
