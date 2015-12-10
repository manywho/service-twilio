package com.manywho.services.twilio;

import com.manywho.sdk.services.config.RedisConfiguration;
import com.manywho.services.twilio.facades.TwilioClientFacade;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.configuration.Redis;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.managers.CallManager;
import com.manywho.services.twilio.managers.CallbackManager;
import com.manywho.services.twilio.managers.MessageManager;
import com.manywho.services.twilio.services.CallService;
import com.manywho.services.twilio.services.CallbackMessageService;
import com.manywho.services.twilio.services.CallbackVoiceService;
import com.manywho.services.twilio.services.MessageService;
import com.manywho.services.twilio.managers.TwimlApplicationManager;
import com.manywho.services.twilio.services.TwilioComponentService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(Redis.class).to(RedisConfiguration.class).in(Singleton.class);
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
