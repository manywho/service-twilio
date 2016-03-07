package com.manywho.services.twilio.services.twiml.read;


import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.services.FlowService;
import com.manywho.services.twilio.services.twiml.PageService;
import com.manywho.services.twilio.services.twiml.TwimlResponseService;
import com.twilio.sdk.verbs.TwiMLResponse;

import javax.inject.Inject;

public class TwimlFromCallRequest {

    final private FlowService flowService;
    final private CacheManager cacheManager;
    final private TwimlResponseService twimlResponseService;
    final private PageService pageService;

    @Inject
    public TwimlFromCallRequest(FlowService flowService, CacheManager cacheManager,
                                TwimlResponseService twimlResponseService, PageService pageService) {
        this.flowService = flowService;
        this.cacheManager = cacheManager;
        this.twimlResponseService = twimlResponseService;
        this.pageService = pageService;
    }

    public TwiMLResponse createTwimlFromCallRequest(String callSid, String stateId, ServiceRequest serviceRequest) throws Exception {

        // Join the flow as we won't have executed it yet in the context of this service
        FlowState flowState = flowService.joinFlow(serviceRequest.getTenantId(), stateId);

        cacheManager.saveFlowExecution(flowState.getStateId(), callSid, flowState);

        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            return twimlResponseService.createTwimlResponseWait(10, flowState.getInvokeResponse(), flowState.getInvokeResponse().getWaitMessage());
        }

        return pageService.createTwimlResponseFromPage(stateId, flowState);
    }
}
