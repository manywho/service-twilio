package com.manywho.services.twilio.entities.verbs;

import com.twilio.sdk.verbs.Verb;

public class Unsupported extends Verb {
    /**
     * We are using this verb for cases when the verb is not supported by ManyWho
     *
     * Instantiates a new verb.
     */
    public Unsupported() {
        super("Unsupported", null);
    }
}
