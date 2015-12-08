package com.manywho.services.twilio.controllers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.twilio.actions.SendMms;
import com.manywho.services.twilio.actions.SendSms;
import com.manywho.services.twilio.actions.SendSmsSimple;
import com.manywho.services.twilio.entities.Configuration;
import com.manywho.services.twilio.types.Mms;
import com.manywho.services.twilio.managers.MessageManager;
import com.manywho.services.twilio.types.Sms;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/messages")
@Consumes("application/json")
@Produces("application/json")
public class MessageController extends AbstractController {

    @Inject
    private MessageManager messageManager;

    @Path("/mms")
    @POST
    @AuthorizationRequired
    public ServiceResponse sendMms(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        SendMms mms = this.parseInputs(serviceRequest, SendMms.class);

        ObjectCollection replyObject = messageManager.sendMms(
                serviceRequest,
                configuration,
                mms.getMessage().getTo(),
                mms.getMessage().getFrom(),
                mms.getMessage().getBody(),
                mms.getMessage().getMedia()
        );

        return new ServiceResponse(
                InvokeType.Wait,
                new EngineValue("Reply", ContentType.Object, Mms.NAME, replyObject),
                serviceRequest.getToken(),
                "Sending an MMS"
        );
    }

    @Path("/sms")
    @POST
    @AuthorizationRequired
    public ServiceResponse sendSms(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        SendSms smsRequest = this.parseInputs(serviceRequest, SendSms.class);

        ObjectCollection replyObject = messageManager.sendSms(
                serviceRequest,
                configuration,
                smsRequest.getMessage().getTo(),
                smsRequest.getMessage().getFrom(),
                smsRequest.getMessage().getBody()
        );

        return new ServiceResponse(
                InvokeType.Wait,
                new EngineValue("Reply", ContentType.Object, Sms.NAME, replyObject),
                serviceRequest.getToken(),
                "Sending an SMS"
        );
    }

    @Path("/smssimple")
    @POST
    @AuthorizationRequired
    public ServiceResponse sendSmsSimple(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        SendSmsSimple smsRequest = this.parseInputs(serviceRequest, SendSmsSimple.class);

        messageManager.sendSms(
                serviceRequest,
                configuration,
                smsRequest.getTo(),
                smsRequest.getFrom(),
                smsRequest.getBody()
        );

        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("From", ContentType.String, ""));
        engineValues.add(new EngineValue("To", ContentType.String, ""));
        engineValues.add(new EngineValue("Body", ContentType.String, ""));

        return new ServiceResponse(InvokeType.Wait, engineValues, serviceRequest.getToken(), "Sending an SMS");
    }
}
