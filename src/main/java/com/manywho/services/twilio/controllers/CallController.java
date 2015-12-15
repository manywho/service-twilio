package com.manywho.services.twilio.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.twilio.actions.StartOutboundCall;
import com.manywho.services.twilio.actions.StartOutboundCallSimple;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.managers.CallManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/calls")
@Consumes("application/json")
@Produces("application/json")
public class CallController extends AbstractController {

    @Inject
    private CallManager callManager;

    @POST
    @Path("/outbound")
    @AuthorizationRequired
    public ServiceResponse startOutboundCall(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        StartOutboundCall startOutboundCall = this.parseInputs(serviceRequest, StartOutboundCall.class);

        callManager.startOutboundCall(
                serviceRequest,
                configuration,
                startOutboundCall.getCall().getFrom(),
                startOutboundCall.getCall().getTo(),
                startOutboundCall.getCall().getTimeout()
        );

        String waitMessage = "Making outbound call to: " + startOutboundCall.getCall().getTo();

        return new ServiceResponse(InvokeType.Wait, serviceRequest.getToken(), waitMessage);
    }

    @POST
    @Path("/outboundsimple")
    @AuthorizationRequired
    public ServiceResponse startOutboundCallSimple(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        StartOutboundCallSimple startOutboundCallSimple = this.parseInputs(serviceRequest, StartOutboundCallSimple.class);

        callManager.startOutboundCall(
                serviceRequest,
                configuration,
                startOutboundCallSimple.getFrom(),
                startOutboundCallSimple.getTo(),
                startOutboundCallSimple.getTimeout()
        );

        String waitMessage = "Making outbound call to: " + startOutboundCallSimple.getTo();

        return new ServiceResponse(InvokeType.Wait, serviceRequest.getToken(), waitMessage);
    }
}
