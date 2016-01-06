package com.manywho.services.twilio.services;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;

import javax.inject.Inject;

public class CallbackVoiceService {
    @Inject
    private RunService runService;

    public InvokeType sendCallResponse(ServiceRequest serviceRequest, String answeredBy) throws Exception {
        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
        serviceResponse.setOutputs(new EngineValueCollection(new EngineValue("Result", ContentType.String, answeredBy)));
        serviceResponse.setTenantId(serviceRequest.getTenantId());

        return runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
    }
}
