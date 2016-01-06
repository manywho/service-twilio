package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.managers.CallbackTranscribeManager;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/callback/transcribe")
public class CallbackTranscribeController {

    @Inject
    private CallbackTranscribeManager callbackTranscribeManager;

    @POST
    @Path("/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    public void voiceFlowCallback(
            @PathParam("stateId") String stateId,
            @BeanParam RecordingCallback recordingCallback
    ) throws Exception {
        callbackTranscribeManager.saveRecordingCallback(stateId, recordingCallback);
    }
}
