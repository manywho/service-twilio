package com.manywho.services.twilio.services.twiml;

import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.types.Recording;
import org.apache.commons.lang3.StringUtils;
import java.util.Objects;
import java.util.UUID;

public class TranscriptionService {


    public Boolean isTranscriptionProcessed(RecordingCallback recordingCallback) {
        return recordingCallback !=null &&
                (
                        Objects.equals(recordingCallback.getTranscriptionStatus(), "completed") ||
                        Objects.equals(recordingCallback.getTranscriptionStatus(), "failed") ||
                        !StringUtils.isEmpty(recordingCallback.getRecordingUrl())
                );
    }

    public Recording getRecording(String recordingUrl, RecordingCallback recordingCallback) {
        Recording recording = new Recording();

        if (StringUtils.isNotEmpty(recordingUrl)) {
            // As this is not a recording callback, we don't have an identifier
            recording.setId(UUID.randomUUID().toString());
            recording.setUrl(recordingUrl);
        } else {
            recording.setId(recordingCallback.getSid());
            recording.setTranscription(recordingCallback.getTranscription());
            recording.setUrl(recordingCallback.getRecordingUrl());
        }

        return recording;
    }
}
