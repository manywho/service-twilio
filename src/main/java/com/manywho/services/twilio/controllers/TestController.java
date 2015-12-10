package com.manywho.services.twilio.controllers;

import com.manywho.sdk.services.config.RedisConfiguration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;

@Path("/")
public class TestController {

    @Inject
    private RedisConfiguration redisConfiguration;

    @Path("/test")
    @GET
    public String test() {
        throw new NotFoundException("ssssssssssssssssssssssss");
    }
}
