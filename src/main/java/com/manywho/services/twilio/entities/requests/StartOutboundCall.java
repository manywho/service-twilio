package com.manywho.services.twilio.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.twilio.entities.types.Call;

import javax.validation.constraints.NotNull;

public class StartOutboundCall {
    @NotNull(message = "A Call object must be provided when a outbound call is created")
    @Property(value = "Call", isObject = true)
    private Call call;

    public Call getCall() {
        return call;
    }
}
