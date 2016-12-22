package com.manywho.services.twilio.services;

import com.google.common.base.Strings;
import com.manywho.services.twilio.entities.Configuration;

import javax.inject.Inject;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public class ForceConfigValuesService {

    private ResourceReader resourceReader;

    @Inject
    public ForceConfigValuesService(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    public Configuration overrideValues(Configuration configuration) {
        try {
            InputStream input = this.resourceReader.getFile("config.value.properties");
            Properties prop = new Properties();
            prop.load(input);
            if (!Strings.isNullOrEmpty(prop.getProperty("accountSid"))) {
                configuration.setAccountSid(prop.getProperty("accountSid"));
            }
            if (!Strings.isNullOrEmpty(prop.getProperty("authToken"))) {
                configuration.setAuthToken(prop.getProperty("authToken"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return  configuration;
    }
}
