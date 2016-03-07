package com.manywho.services.twilio.managers;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.services.FlowService;
import com.manywho.services.twilio.services.twiml.TwimlResponseService;
import com.manywho.services.twilio.services.twiml.read.TwimlFromCallRequest;
import com.manywho.services.twilio.services.twiml.read.TwimlFromFlow;
import com.manywho.services.twilio.services.twiml.read.TwimlFromInvoke;
import com.twilio.sdk.verbs.*;
import javax.inject.Inject;

public class CallbackTwimlManager {
    final private CacheManager cacheManager;
    final private FlowService flowService;
    final private TwimlFromFlow twimlFromFlow;
    final private TwimlFromInvoke twimlFromInvoke;
    final private TwimlFromCallRequest twimlFromCallRequest;
    final private TwimlResponseService twimlResponseService;

    @Inject
    public CallbackTwimlManager(CacheManager cacheManager, FlowService flowService,
                                TwimlFromFlow twimlFromFlow, TwimlFromInvoke twimlFromInvoke,
                                TwimlFromCallRequest twimlFromCallRequest, TwimlResponseService twimlResponseService) throws Exception {

        this.cacheManager = cacheManager;
        this.flowService = flowService;
        this.twimlFromInvoke = twimlFromInvoke;
        this.twimlFromFlow = twimlFromFlow;
        this.twimlFromCallRequest = twimlFromCallRequest;
        this.twimlResponseService = twimlResponseService;
    }

    public TwiMLResponse continueFlowAsTwiml(String stateId, String callSid, String digits, String recordingUrl) throws Exception {
        TwiMLResponse response;

        if (cacheManager.hasFlowExecution(stateId, callSid)) {
            // If we have a stored flow state then create TwiML based on that
            FlowState flowState = cacheManager.getFlowExecution(stateId, callSid);
            flowService.syncIfStatusWait(flowState);

            response = twimlFromFlow.createTwimlFromFlow(callSid, stateId, digits, recordingUrl, flowState);
            saveInCacheIfHungUpVerbExist(response, callSid);

            return response;
        } else if (cacheManager.hasCallRequest(callSid)) {
            // If we have a call request stored, create TwiML based on that
            // Fetch the cached ServiceRequest for the given Call SID
            ServiceRequest serviceRequest = cacheManager.getCallRequest(callSid);
            response = twimlFromCallRequest.createTwimlFromCallRequest(callSid, stateId, serviceRequest);
            saveInCacheIfHungUpVerbExist(response, callSid);

            return response;
        }

        throw new Exception("Unable to continue the flow as no stored requests are found for the SID " + callSid);
    }

    public TwiMLResponse startFlowAsTwiml(String tenantId, String flowId, String callSid) throws Exception {
        // Generate the TwiML for the call
        FlowState flowState = flowService.startFlow(tenantId, flowId);

        return twimlFromInvoke.generateTwimlForInvoke(callSid, flowState);
    }

    private void saveInCacheIfHungUpVerbExist(TwiMLResponse response, String callSid) throws TwiMLException {

        if (twimlResponseService.hasHangUp(response)) {
            cacheManager.saveCallHungupByTwiml(callSid);
        }
    }
}
