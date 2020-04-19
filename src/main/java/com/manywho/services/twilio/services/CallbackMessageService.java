package com.manywho.services.twilio.services;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.types.Sms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;

public class CallbackMessageService {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Inject
    private RunService runService;

    @Inject
    private ObjectMapperService objectMapperService;

    @Inject
    private CacheManager cacheManager;

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

    public void sendMessageReplyResponse(ServiceRequest serviceRequest, String messageSid, String from, String to, String body) throws Exception {
        EngineValueCollection replyOutput = new EngineValueCollection();

        LOGGER.debug("Processing reply from {} to {}", from, to);
        // If the action was SendSmsSimple, just add a "Reply" string as the output, otherwise use an Sms object
        if (serviceRequest.getUri().endsWith("simple")) {
            replyOutput.add(new EngineValue("Reply", ContentType.String, body));
        } else {
            MObject smsObject = objectMapperService.convertSmsToObject(new Sms(messageSid, null, from, body));

            replyOutput.add(new EngineValue("Reply", ContentType.Object, Sms.NAME, smsObject));
        }
        cacheManager.deleteStateWaitingForSms(to+from);
        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, replyOutput, serviceRequest.getToken());
        serviceResponse.setTenantId(serviceRequest.getTenantId());

        InvokeType invokeType = runService.sendResponse(null, null, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse);
        LOGGER.debug("Send response to engine {}", invokeType);
    }
}
