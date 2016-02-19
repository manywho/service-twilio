package com.manywho.services.twilio.controllers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.services.twilio.managers.DataManager;
import com.manywho.services.twilio.types.CallRecording;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DataController extends AbstractDataController {

    @Inject
    DataManager dataManager;

    @Override
    public ObjectDataResponse delete(ObjectDataRequest objectDataRequest) throws Exception {
        throw new Exception("Deleting isn't currently supported in the Twilio Service");
    }

    @Path("/data")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse load(ObjectDataRequest objectDataRequest) throws Exception {

        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            case CallRecording.NAME:
                return new ObjectDataResponse(dataManager.loadRecordingForCall(objectDataRequest));
            default:
                // Assume the type represents Metadata
                throw new Exception("This data isn't currently supported in the Box Service");
        }
    }

    @Path("/data")
    @PUT
    @AuthorizationRequired
    public ObjectDataResponse save(ObjectDataRequest objectDataRequest) throws Exception {
        throw new Exception("Deleting isn't currently supported in the Twilio Service");
    }
}