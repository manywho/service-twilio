package com.manywho.services.twilio.managers;

import com.manywho.services.twilio.entities.RecordingCallback;
import javax.inject.Inject;

public class CallbackTranscribeManager {
    final private CacheManager cacheManager;

    @Inject
    public CallbackTranscribeManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void saveRecordingCallback(String stateId, RecordingCallback recordingCallback) throws Exception {
        cacheManager.saveRecordingCallback(stateId, recordingCallback.getCallSid(), recordingCallback);
    }
}
