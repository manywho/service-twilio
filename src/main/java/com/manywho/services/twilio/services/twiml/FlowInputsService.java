package com.manywho.services.twilio.services.twiml;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.PageComponent;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequest;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.exception.WaitingForSomethingException;
import com.manywho.services.twilio.exception.WaitingForTranscriptionException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Optional;

import static com.manywho.sdk.client.utils.PageComponentUtils.getFirstComponentWithType;

public class FlowInputsService {
    final private TwilioComponentService twilioComponentService;
    final private TranscriptionService transcriptionService;

    @Inject
    public FlowInputsService(TwilioComponentService twilioComponentService,
                             TranscriptionService transcriptionService) {
        this.twilioComponentService = twilioComponentService;
        this.transcriptionService = transcriptionService;
    }

    // Only fill in the first input, as we can't match up the digits with the desired one if there's more than 1
    public void addDigitsToInputs(PageComponentInputResponseRequestCollection inputs, FlowState flowState, String digits) {
        Optional<PageComponent> inputComponent = getFirstComponentWithType(flowState.getPageComponents(), "INPUT");
        if (inputComponent.isPresent()) {
            inputs.add(new PageComponentInputResponseRequest(inputComponent.get().getId(), digits));
        }
    }

    public void addRecordingToInputs(PageComponentInputResponseRequestCollection inputs, FlowState flowState,
                                     String recordingUrl, RecordingCallback recordingCallback) throws Exception {

        if (recordingCallback != null || StringUtils.isNotEmpty(recordingUrl)) {
            Optional<PageComponent> voiceComponent = getFirstComponentWithType(flowState.getPageComponents(), "Record");

            if (voiceComponent.isPresent()) {

                if (twilioComponentService.isTranscriptionSelected(voiceComponent.get()) &&
                        !transcriptionService.isTranscriptionProcessed(recordingCallback)) {
                            throw new WaitingForTranscriptionException();
                }

                inputs.add(twilioComponentService.getInputResponseRequestRecording(voiceComponent.get(), recordingUrl, recordingCallback));
            }
        } else {
            throw new WaitingForSomethingException();
        }
    }
}
