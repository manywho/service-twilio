package com.manywho.services.twilio.controllers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.twilio.actions.StartOutboundCall;
import com.manywho.services.twilio.actions.StartOutboundCallSimple;
import com.manywho.services.twilio.actions.VoiceSimple;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.managers.CallManager;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.net.URLDecoder;

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

        String callSid = callManager.startOutboundCall(
                serviceRequest,
                configuration,
                startOutboundCall.getCall().getFrom(),
                startOutboundCall.getCall().getTo(),
                startOutboundCall.getCall().getTimeout(),
                startOutboundCall.getCall().getRecord()
        );

        String waitMessage = "Making outbound call to: " + startOutboundCall.getCall().getTo();

        ServiceResponse serviceResponse = new ServiceResponse();
        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("Call Sid", ContentType.String, callSid));

        serviceResponse.setOutputs(engineValues);
        serviceResponse.setToken(serviceRequest.getToken());
        serviceResponse.setInvokeType(InvokeType.Forward);
        serviceResponse.setWaitMessage(waitMessage);

        return serviceResponse;
    }

    @POST
    @Path("/outboundsimple")
    @AuthorizationRequired
    public ServiceResponse startOutboundCallSimple(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        StartOutboundCallSimple startOutboundCallSimple = this.parseInputs(serviceRequest, StartOutboundCallSimple.class);

        String timeout = startOutboundCallSimple.getTimeout();
        //todo remove this property in twilio version 3
        if(StringUtils.isEmpty(timeout)) {
            timeout = "60";
        }

        String callSid = callManager.startOutboundCall(
                serviceRequest,
                configuration,
                startOutboundCallSimple.getFrom(),
                startOutboundCallSimple.getTo(),
                timeout,
                startOutboundCallSimple.getRecord()
        );

        String waitMessage = "Making outbound call to: " + startOutboundCallSimple.getTo();

        ServiceResponse serviceResponse = new ServiceResponse();
        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("Call Sid", ContentType.String, callSid));

        serviceResponse.setOutputs(engineValues);
        serviceResponse.setToken(serviceRequest.getToken());
        serviceResponse.setInvokeType(InvokeType.Forward);
        serviceResponse.setWaitMessage(waitMessage);

        return serviceResponse;
    }

    @POST
    @Path("/voicesimple")
    @AuthorizationRequired
    public ServiceResponse voiceSimple(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        VoiceSimple voiceSimple = this.parseInputs(serviceRequest, VoiceSimple.class);

        String voice = voiceSimple.getVoice();
        if(StringUtils.isEmpty(voice)) {
            voice = "man";
        }

        String language = voiceSimple.getLanguage();
        if(StringUtils.isEmpty(language)) {
            language = "en";
        }

        callManager.voiceMessage(
            serviceRequest,
            configuration,
            voiceSimple.getFrom(),
            voiceSimple.getTo(),
            voiceSimple.getMessage(),
            voice,
            language
        );

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setToken(serviceRequest.getToken());
        serviceResponse.setInvokeType(InvokeType.Forward);

        return serviceResponse;
    }
}
