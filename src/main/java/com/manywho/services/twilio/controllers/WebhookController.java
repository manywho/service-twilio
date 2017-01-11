package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.managers.WebhookManager;
import com.manywho.services.twilio.services.FlowService;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback")
public class WebhookController {

    @Inject
    private FlowService flowService;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private WebhookManager webhookManager;

    @POST
    @Path("/sms/flow/{tenantId}/{flowId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public void smsFlowCallback(
            @PathParam("tenantId") String tenantId,
            @PathParam("flowId") String flowId,
            @BeanParam MessageCallback messageCallback
            ) throws Exception {

        // if there isn't any flow waitting then I can execute the execute the flow
        if(cacheManager.getStateWaitingForSms(messageCallback.getTo()+ messageCallback.getFrom()) == null) {
            cacheManager.saveSmsWebhook(messageCallback);
            flowService.initializeAndExecuteFlow(flowId, null, tenantId, messageCallback);
        } else {
            webhookManager.handleMessageStatus(messageCallback);
        }
    }
}
