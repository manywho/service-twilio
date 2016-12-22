package com.manywho.services.twilio;

import com.manywho.sdk.client.FlowClient;
import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.factories.FlowClientFactory;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.*;
import com.manywho.services.twilio.services.*;
import com.manywho.services.twilio.services.twiml.*;
import com.manywho.services.twilio.services.twiml.read.TwimlFromCallRequest;
import com.manywho.services.twilio.services.twiml.read.TwimlFromFlow;
import com.manywho.services.twilio.services.twiml.read.TwimlFromInvoke;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(TwilioConfiguration.class).to(TwilioConfiguration.class).in(Singleton.class);
        bind(TwilioRestClientFactory.class).to(TwilioRestClientFactory.class).in(Singleton.class);
        bind(CacheManager.class).to(CacheManager.class);
        bind(CallManager.class).to(CallManager.class);
        bind(CallService.class).to(CallService.class);
        bind(CallbackManager.class).to(CallbackManager.class);
        bind(CallbackTranscribeManager.class).to(CallbackTranscribeManager.class);
        bind(CallbackTwimlManager.class).to(CallbackTwimlManager.class);
        bind(CallbackMessageService.class).to(CallbackMessageService.class);
        bind(CallbackVoiceService.class).to(CallbackVoiceService.class);
        bind(FlowService.class).to(FlowService.class);
        bind(MessageManager.class).to(MessageManager.class);
        bind(MessageManager.class).to(MessageManager.class);
        bind(MessageService.class).to(MessageService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(RawRunClient.class).to(RawRunClient.class);
        bindFactory(FlowClientFactory.class).to(FlowClient.class);
        bind(TwilioComponentService.class).to(TwilioComponentService.class);
        bind(DataManager.class).to(DataManager.class);
        bind(TwimlResponseService.class).to(TwimlResponseService.class);
        bind(PageService.class).to(PageService.class);
        bind(TranscriptionService.class).to(TranscriptionService.class);
        bind(FlowInputsService.class).to(FlowInputsService.class);
        bind(TwimlFromFlow.class).to(TwimlFromFlow.class);
        bind(TwimlFromInvoke.class).to(TwimlFromInvoke.class);
        bind(TwimlFromCallRequest.class).to(TwimlFromCallRequest.class);
        bind(WebhookManager.class).to(WebhookManager.class);
        bind(ForceConfigValuesService.class).to(ForceConfigValuesService.class);
        bind(ResourceReader.class).to(ResourceReader.class);
    }
}
