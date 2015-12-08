package com.manywho.services.twilio.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.services.twilio.types.Call;

import javax.validation.constraints.NotNull;

@Action(name = "Start Outbound Call", summary = "Start an outbound phone call", uriPart = "calls/outbound")
public class StartOutboundCall {
    @NotNull(message = "A Call object must be provided when a outbound call is created")
    @ActionInput(name = "Call", contentType = ContentType.Object)
    private Call call;

    public Call getCall() {
        return call;
    }
}
