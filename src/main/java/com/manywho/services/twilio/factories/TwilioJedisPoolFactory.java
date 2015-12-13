package com.manywho.services.twilio.factories;

import com.manywho.sdk.services.config.RedisConfiguration;
import org.glassfish.hk2.api.Factory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

public class TwilioJedisPoolFactory implements Factory<JedisPool> {
    @Inject
    private RedisConfiguration redisConfiguration;

    @Override
    public JedisPool provide() {
        JedisPool pool = new JedisPool("redis://h:pu4barko0h09pegoabiafi2b80@ec2-107-22-174-233.compute-1.amazonaws.com:7409");

        // Initialize the maximum number of idle connections to Redis, instead of connecting lazily
        pool.addObjects(JedisPoolConfig.DEFAULT_MAX_IDLE);

        return pool;
    }

    @Override
    public void dispose(JedisPool jedisPool) {
        jedisPool.destroy();
    }
}
