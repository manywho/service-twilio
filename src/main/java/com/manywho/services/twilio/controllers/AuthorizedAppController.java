package com.manywho.services.twilio.controllers;

import javax.ws.rs.*;

@Path("/authorized")
@Consumes("application/json")
@Produces("application/json")
public class AuthorizedAppController {
    @GET
    @Produces("text/html")
    @Path("/app")
    public String authorizedApp(@QueryParam("AccountSid") String accountSid){
        /**
         * this entry-point is called by twilio app after the user have authorized the application
         * the authorization point is the one generate by twilio using Generate Connect Button HTML
         */
        // we need to redirect in this point
        return accountSid;
    }
}
