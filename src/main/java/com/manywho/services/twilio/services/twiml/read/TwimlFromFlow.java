package com.manywho.services.twilio.services.twiml.read;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.sdk.entities.run.elements.ui.PageComponentInputResponseRequestCollection;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.exception.WaitingForSomethingException;
import com.manywho.services.twilio.exception.WaitingForTranscriptionException;
import com.manywho.services.twilio.managers.CacheManager;
import com.manywho.services.twilio.services.FlowService;
import com.manywho.services.twilio.services.twiml.FlowInputsService;
import com.manywho.services.twilio.services.twiml.PageService;
import com.manywho.services.twilio.services.twiml.TwilioComponentService;
import com.manywho.services.twilio.services.twiml.TwimlResponseService;
import com.twilio.sdk.verbs.TwiMLResponse;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static com.manywho.sdk.client.utils.PageComponentUtils.doInputComponentsExist;
import static com.manywho.sdk.client.utils.PageComponentUtils.doesComponentWithTypeExist;

public class TwimlFromFlow {
    final private static List<String> inputComponentTypes = Collections.singletonList("Record");
    final private FlowInputsService flowInputsService;
    final private TwimlResponseService twimlResponseService;
    final private FlowService flowService;
    final private PageService pageService;
    final private CacheManager cacheManager;
    final private TwimlFromInvoke twimlFromInvoke;

    @Inject
    public TwimlFromFlow(FlowInputsService flowInputsService, TwimlResponseService twimlResponseService,
                         FlowService flowService, PageService pageService, CacheManager cacheManager, TwimlFromInvoke twimlFromInvoke) {
        this.flowInputsService = flowInputsService;
        this.twimlResponseService = twimlResponseService;
        this.flowService = flowService;
        this.pageService = pageService;
        this.cacheManager = cacheManager;
        this.twimlFromInvoke = twimlFromInvoke;
    }

    public TwiMLResponse createTwimlFromFlow(String callSid, String stateId, String digits, String recordingUrl, FlowState flowState, TwilioComponentService.CallbackType callbackType) throws Exception {

        // If the flow still has a WAIT status, then return some TwiML to say that we're still waiting
        if (flowState.getInvokeType().equals(InvokeType.Wait)) {
            return twimlResponseService.createTwimlResponseWait(10, flowState.getInvokeResponse(), flowState.getInvokeResponse().getWaitMessage());
        }

        // If there are no outcomes, then just speak the current step as normal
        if (!flowState.hasOutcomes()) {
            return pageService.createTwimlResponseFromPage(stateId, flowState, callbackType);
        }

        Optional<Outcome> outcomeForDigits = flowState.getOutcomes().stream()
                .filter(outcome -> outcome.getName().equals(digits))
                .findFirst();

        boolean inputsRecordExist = doInputComponentsExist(flowState.getPageComponents(), inputComponentTypes);

        // If there are no inputs, and the digits don't match with an outcome then repeat the current step as TwiML
        if (!outcomeForDigits.isPresent() && !inputsRecordExist) {
            return  twimlFromInvoke.generateTwimlForInvoke(callSid, flowState, callbackType);
        }

        PageComponentInputResponseRequestCollection inputs = new PageComponentInputResponseRequestCollection();

        if (inputsRecordExist) {
            // Only fill in the first input, as we can't match up the digits with the desired one if there's more than 1
            flowInputsService.addDigitsToInputs(inputs, flowState, digits);
        }

        if (doesComponentWithTypeExist(flowState.getPageComponents(), "Record")) {
            try {
                RecordingCallback recordingCallback = null;

                // has twilio call to the service with the transcription result?
                if (cacheManager.hasRecordingCallback(flowState.getStateId(), callSid)) {
                    recordingCallback = cacheManager.getRecordingCallback(flowState.getStateId(), callSid);
                }

                flowInputsService.addRecordingToInputs(inputs, flowState, recordingUrl, recordingCallback);
                cacheManager.deleteRecordingCallback(flowState.getStateId(), callSid);

            } catch (WaitingForTranscriptionException ex) {
                return twimlResponseService.createTwimlResponseWait(0, flowState.getInvokeResponse(), "Waiting for transcription");
            } catch (WaitingForSomethingException ex) {
                return twimlResponseService.createTwimlResponseWait(10, flowState.getInvokeResponse(), "");
            }
        }

        // If there is still no outcome then just get the first one
        if (!outcomeForDigits.isPresent()) {
            outcomeForDigits = flowState.getOutcomes().stream().findFirst();
        }

        FlowState nextStepState = flowService.progressToNextStep(flowState, outcomeForDigits.get(), inputs, InvokeType.Forward);
        // Updated the stored flow state with the new state
        cacheManager.saveFlowExecution(flowState.getStateId(), callSid, flowState);

        if (nextStepState.getInvokeType().equals(InvokeType.Wait)) {
            return twimlResponseService.createTwimlResponseWait(10, nextStepState.getInvokeResponse(), nextStepState.getInvokeResponse().getWaitMessage());
        }

        return pageService.createTwimlResponseFromPage(stateId, nextStepState, callbackType);
    }

}
