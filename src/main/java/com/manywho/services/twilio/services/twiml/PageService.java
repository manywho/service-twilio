package com.manywho.services.twilio.services.twiml;

import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.entities.Outcome;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.twilio.sdk.verbs.*;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.manywho.sdk.client.utils.PageComponentUtils.doesComponentWithTypeExist;

public class PageService {
    final private TwilioComponentService twilioComponentService;
    final private TwilioConfiguration twilioConfiguration;

    @Inject
    public PageService(TwilioComponentService twilioComponentService, TwilioConfiguration twilioConfiguration) {
        this.twilioComponentService = twilioComponentService;
        this.twilioConfiguration = twilioConfiguration;
    }

    public TwiMLResponse createTwimlResponseFromPage(String stateId, FlowState flowState, TwilioComponentService.CallbackType callbackType) throws TwiMLException {
        TwiMLResponse twiMLResponse = new TwiMLResponse();

        // Create TwiML components from all the PageComponents
        List<Verb> twimlComponents = flowState.getPageComponents().stream()
                .map(component -> twilioComponentService.createTwimlForComponent(component, stateId, callbackType))
                .collect(Collectors.toList());

        // If there are outcomes and we should auto wrap in a gather, then we do that
        if (flowState.hasOutcomes() && !doesComponentWithTypeExist(flowState.getPageComponents(), "Record")) {
            Gather gather = new Gather();
            gather.setAction(this.twilioConfiguration.getCallbackTwimlVoiceFlowState() + stateId);


            Optional<Outcome> longestNamedOutcome = flowState.getOutcomes().stream().
                    filter(outcome -> StringUtils.isNumeric(outcome.getName()) )
                    .max(Comparator.comparing(outcome -> outcome.getName().length()));

            if(longestNamedOutcome.isPresent()) {
                gather.setNumDigits(longestNamedOutcome.get().getName().length());
            }

            // Add all the TwiML components to the Gather
            twimlComponents.stream()
                    .forEach(component -> gather.getChildren().add(component));

            twiMLResponse.append(gather);

            if (doesComponentWithTypeExist(flowState.getPageComponents(), "Hangup")) {
                twiMLResponse.append(new Hangup());
            } else {

                // Automatically append a pause and join in case they need to re-hear the message
                twiMLResponse.append(twilioComponentService.createPauseComponent(10));
                if (callbackType == TwilioComponentService.CallbackType.PHONE_CALL_CALLBACK) {
                    twiMLResponse.append(twilioComponentService.createRedirectComponent(
                            this.twilioConfiguration.getCallbackTwimlVoiceFlowState() + stateId));
                } else{
                    twiMLResponse.append(twilioComponentService.createRedirectComponent(
                            this.twilioConfiguration.getCallbackTwimlSmsFlowState() + stateId));
                }
            }
        } else {
            // Add all the non-null TwiML components to the response
            twimlComponents.stream()
                    .forEach(component -> twiMLResponse.getChildren().add(component));
        }

        return twiMLResponse;
    }
}
