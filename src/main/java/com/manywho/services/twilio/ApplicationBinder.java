package com.manywho.services.twilio;

import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.facades.TwilioClientFacade;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.*;
import com.manywho.services.twilio.services.*;
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
        bind(CallbackMessageService.class).to(CallbackMessageService.class);
        bind(CallbackVoiceService.class).to(CallbackVoiceService.class);
        bind(MessageManager.class).to(MessageManager.class);
        bind(MessageService.class).to(MessageService.class);
        bind(TwilioComponentService.class).to(TwilioComponentService.class);
        bind(TwimlApplicationManager.class).to(TwimlApplicationManager.class);
        bind(TwilioClientFacade.class).to(TwilioClientFacade.class);
    }
}
