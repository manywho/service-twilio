package com.manywho.services.twilio.entities;

import com.manywho.sdk.services.annotations.Property;
import org.hibernate.validator.constraints.NotBlank;

public class Configuration {
    @NotBlank(message = "The Account SID configuration value must not be null or blank")
    @Property("Account SID")
    private String accountSid;

    @NotBlank(message = "The Auth Token configuration value must not be null or blank")
    @Property("Auth Token")
    private String authToken;

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }
}
