package com.manywho.services.twilio.services.twiml.read;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.services.twiml.PageService;
import com.manywho.services.twilio.services.twiml.TwilioComponentService;
import com.twilio.sdk.verbs.TwiMLResponse;

import javax.inject.Inject;

public class TwimlFromInvoke {
    final private CacheManager cacheManager;
    final private PageService pageService;

    @Inject
    public TwimlFromInvoke(CacheManager cacheManager, PageService pageService) {
        this.cacheManager = cacheManager;
        this.pageService = pageService;
    }

    public TwiMLResponse generateTwimlForInvoke(String callSid, FlowState flowState, TwilioComponentService.CallbackType callbackType) throws Exception {
        cacheManager.saveFlowExecution(flowState.getState().toString(), callSid, flowState);

        // Check if there are any components in the returned Page Response, as we need to send one in the next invoke
        if (!flowState.hasPageComponents()) {
            throw new Exception("There are no components in the current step");
        }

        return pageService.createTwimlResponseFromPage(flowState.getState().toString(), flowState, callbackType);
    }


}
