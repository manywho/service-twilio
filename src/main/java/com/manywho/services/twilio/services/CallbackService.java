package com.manywho.services.twilio.services;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.types.Sms;

import javax.inject.Inject;
import java.util.HashMap;

public class CallbackService {

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

    public InvokeType sendMessageResponse(ServiceRequest serviceRequest, InvokeType invokeType, String waitMessageText, String errorMessageText) throws Exception {
        return runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), new ServiceResponse() {{
            setInvokeType(invokeType);

            if (errorMessageText != null) {
                setRootFaults(new HashMap<String, String>() {{
                    put("error", errorMessageText);
                }});
            }

            setTenantId(serviceRequest.getTenantId());
            setToken(serviceRequest.getToken());

            if (waitMessageText != null) {
                setWaitMessage(waitMessageText);
            }
        }});
    }

    public InvokeType sendMessageReplyResponse(ServiceRequest serviceRequest, String messageSid, String from, String body) throws Exception {
        EngineValueCollection replyOutput = new EngineValueCollection();

        // If the action was SendSmsSimple, just add a "Reply" string as the output, otherwise use an Sms object
        if (serviceRequest.getUri().endsWith("simple")) {
            replyOutput.add(new EngineValue("Reply", ContentType.String, body));
        } else {
            replyOutput.add(new EngineValue("Reply", ContentType.Object, Sms.NAME, new ObjectCollection() {{
                add(new Object() {{
                    setDeveloperName(Sms.NAME);
                    setExternalId(messageSid);
                    setProperties(new PropertyCollection() {{
                        add(new Property("From", from));
                        add(new Property("Body", body));
                    }});
                }});
            }}));
        }

        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, replyOutput, serviceRequest.getToken());
        serviceResponse.setTenantId(serviceRequest.getTenantId());

        return runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
    }
}
