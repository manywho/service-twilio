package com.manywho.services.twilio.controllers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthController {
    private final JedisPool jedisPool;

    @Inject
    public HealthController(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @GET
    public Response healthCheck() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
        }

        return Response.ok().build();
    }
}
