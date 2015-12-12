package com.manywho.services.twilio.entities;

import javax.ws.rs.FormParam;

public class RecordingCallback {
    @FormParam("TranscriptionText")
    private String transcription;

    @FormParam("RecordingUrl")
    private String recordingUrl;

    @FormParam("Sid")
    private String sid;

    @FormParam("CallSid")
    private String callSid;

    public String getTranscription() {
        return transcription;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public String getSid() {
        return sid;
    }

    public String getCallSid() {
        return callSid;
    }
}
