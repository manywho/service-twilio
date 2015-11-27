package com.manywho.services.twilio.entities;

import javax.ws.rs.FormParam;

public class VoiceCallback {
    @FormParam("AnsweredBy")
    private String answeredBy;

    @FormParam("CallSid")
    private String callSid;

    @FormParam("Direction")
    private String direction;

    public String getAnsweredBy() {
        return answeredBy;
    }

    public String getCallSid() {
        return callSid;
    }

    public String getDirection() {
        return direction;
    }
}
