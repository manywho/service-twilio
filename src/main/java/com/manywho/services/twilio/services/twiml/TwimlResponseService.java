package com.manywho.services.twilio.services.twiml;
import com.manywho.sdk.entities.run.EngineInvokeResponse;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.Verb;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class TwimlResponseService {

    final private TwilioComponentService twilioComponentService;
    final private TwilioConfiguration twilioConfiguration;

    @Inject
    public TwimlResponseService (TwilioComponentService twilioComponentService, TwilioConfiguration twilioConfiguration) {
        this.twilioComponentService = twilioComponentService;
        this.twilioConfiguration = twilioConfiguration;
    }

    public Boolean hasHangUp(TwiMLResponse twimlResponse) {
        Optional optional = twimlResponse
                .getChildren()
                .stream()
                .filter(verb -> Objects.equals(verb.getTag(), Verb.V_HANGUP))
                .findFirst();

        return optional.isPresent();
    }

    public TwiMLResponse createTwimlResponseWait(int pause, EngineInvokeResponse engineInvokeResponse, String message) throws Exception {
        TwiMLResponse waitResponse = new TwiMLResponse();

        if(!StringUtils.isEmpty(message)) {
            waitResponse.append(new Say(message));
        }
        if( pause != 0) {
            waitResponse.append(twilioComponentService.createPauseComponent(pause));
        }

        waitResponse.append(twilioComponentService.createRedirectComponent(
                twilioConfiguration.getManyWhoTwiMLAppConfiguration().get("CallbackTwimlVoiceFlowState") + engineInvokeResponse.getStateId()
        ));

        return waitResponse;
    }
}
