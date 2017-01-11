package com.manywho.services.twilio.controllers;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * The path for this controller have been added by mistake use instead the methods in CallbackTwimlController
 *
 * @deprecated use {@link CallbackTwimlController} instead
 *
 */
@Path("/callback/callbackTwiml")
@Deprecated()
public class CallbackTwimlDeprecatedController {

    @Inject
    CallbackTwimlController callbackTwimlController;

    /**
     * @deprecated use {@link CallbackTwimlController} instead
     */
    @Deprecated()
    @POST
    @Path("/voice/flow/{tenantId}/{flowId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceFlowCallback(
            @PathParam("tenantId") String tenantId,
            @PathParam("flowId") String flowId,
            @FormParam("CallSid") String callSid,
            @FormParam("Direction") String direction
    ) throws Exception {
        return callbackTwimlController.voiceFlowCallback(tenantId, flowId, callSid, direction);
    }

    /**
     *
     * This entry point will return a twiml (xml) when is called by twilio,
     * When we are waiting for some information, the response will have a child <Pause length="x"/> and this entry point
     * will be called in x seconds by twilio again, then we can update the xml with the new information (if the information
     * is not ready we will return again a pause child).
     *
     * When the call finish this entry point is not called any more.
     *
     * @deprecated use {@link CallbackTwimlController} instead
     */
    @Deprecated()
    @POST
    @Path("/voice/flow/state/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/xml")
    public String voiceFlowStateCallback(
            @PathParam("stateId") String stateId,
            @FormParam("CallSid") String callSid,
            @FormParam("Digits") String digits,
            @FormParam("RecordingUrl") String recordingUrl
    ) throws Exception {
        return this.callbackTwimlController.voiceFlowStateCallback(stateId, callSid, digits, recordingUrl);
    }
}
