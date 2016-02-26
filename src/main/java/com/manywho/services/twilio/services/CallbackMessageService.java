package com.manywho.services.twilio.services;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.types.Sms;

import javax.inject.Inject;

public class CallbackMessageService {

    @Inject
    private RunService runService;

    @Inject
    private ObjectMapperService objectMapperService;

    public InvokeType sendMessageResponse(ServiceRequest serviceRequest, String waitMessageText, String errorMessageText) throws Exception {
        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
        serviceResponse.setTenantId(serviceRequest.getTenantId());

        if (errorMessageText != null) {
            serviceResponse.addRootFault("error", errorMessageText);
        }

        if (waitMessageText != null) {
            serviceResponse.setWaitMessage(waitMessageText);
        }

        return runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
    }

    public void sendMessageReplyResponse(ServiceRequest serviceRequest, String messageSid, String from, String body) throws Exception {
        EngineValueCollection replyOutput = new EngineValueCollection();

        // If the action was SendSmsSimple, just add a "Reply" string as the output, otherwise use an Sms object
        if (serviceRequest.getUri().endsWith("simple")) {
            replyOutput.add(new EngineValue("Reply", ContentType.String, body));
        } else {
            MObject smsObject = objectMapperService.convertSmsToObject(new Sms(messageSid, null, from, body));

            replyOutput.add(new EngineValue("Reply", ContentType.Object, Sms.NAME, smsObject));
        }

        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, replyOutput, serviceRequest.getToken());
        serviceResponse.setTenantId(serviceRequest.getTenantId());

        runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
    }
}