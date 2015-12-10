package com.manywho.services.twilio.controllers;

import com.manywho.services.twilio.managers.CallbackManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/callback/transcribe")
public class CallbackTranscribeController {

    @Inject
    private CallbackManager callbackManager;

    @POST
    @Path("/{stateId}")
    @Consumes("application/x-www-form-urlencoded")
    public void voiceFlowCallback(
            @PathParam("stateId") String stateId,
            @FormParam("TranscriptionText") String transcriptionText
    ) throws Exception {
        callbackManager.saveTranscription(stateId, transcriptionText);
    }
}
