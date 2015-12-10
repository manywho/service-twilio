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
        return runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), new ServiceResponse() {{
            setInvokeType(InvokeType.Forward);
            setOutputs(new EngineValueCollection() {{
                add(new EngineValue("Result", ContentType.String, answeredBy));
            }});
            setTenantId(serviceRequest.getTenantId());
            setToken(serviceRequest.getToken());
        }});
    }
}
