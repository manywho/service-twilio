package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/callback/transcribe")
public class CallbackTranscribeController {

    @Inject
    private CallbackManager callbackManager;

    @POST
    @Path("/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    public void voiceFlowCallback(
            @PathParam("stateId") String stateId,
            @BeanParam RecordingCallback recordingCallback
    ) throws Exception {
        callbackManager.saveRecordingCallback(stateId, recordingCallback);
    }
}
