package com.manywho.services.twilio.configuration;

import com.manywho.sdk.services.config.RedisConfiguration;
import com.manywho.sdk.services.config.ServiceConfiguration;

import javax.inject.Inject;

public class Redis implements RedisConfiguration {

    @Inject
    private ServiceConfiguration configuration;

    @Override
    public String getEndpoint() {
        return configuration.get("redis.url");
    }
}