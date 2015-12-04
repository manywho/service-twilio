package com.manywho.services.twilio;

import com.manywho.sdk.services.config.RedisConfiguration;
import com.manywho.sdk.services.factories.JedisPoolFactory;
import com.manywho.sdk.services.providers.ExceptionMapperProvider;
import com.manywho.services.twilio.facades.TwilioClientFacade;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.configuration.Redis;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.managers.CallManager;
import com.manywho.services.twilio.managers.CallbackManager;
import com.manywho.services.twilio.managers.MessageManager;
import com.manywho.services.twilio.services.CallService;
import com.manywho.services.twilio.services.CallbackService;
import com.manywho.services.twilio.services.MessageService;
import com.manywho.services.twilio.managers.TwimlApplicationManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import redis.clients.jedis.JedisPool;

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
        bind(CallbackService.class).to(CallbackService.class);
        bind(MessageManager.class).to(MessageManager.class);
        bind(MessageService.class).to(MessageService.class);
        bind(TwimlApplicationManager.class).to(TwimlApplicationManager.class);
        bind(TwilioClientFacade.class).to(TwilioClientFacade.class);
    }
}
