package com.manywho.services.twilio.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.manywho.services.twilio.managers.CacheManager;
import com.twilio.sdk.resource.instance.Call;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoiceTest extends TwilioServiceFunctionalTest {

    @Test
    public void testVoiceSimple() throws Exception {
        when(mockTwilioClientFactory.createTwilioRestClient("mockAppSid","mockAuthToken"))
                .thenReturn(mockTwilioRestClient);

        final Map<String, String> callParameters = new HashMap<>();

        callParameters.put("To", "+441234567899");
        callParameters.put("From", "+440123456789");
        callParameters.put("Timeout", "60");
        callParameters.put("IfMachine", "Hangup");
        callParameters.put("Record", "false");
        callParameters.put("Url", "");
        Call mockCall = mock(Call.class);

        when(mockCall.getSid()).thenReturn("1234");
        when(mockCallFactory.create(callParameters)).thenReturn(mockCall);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/voicesimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("VoiceTest/voice1-request-ok.json"));

        assertJsonSame(
                getJsonFormatFileContent("VoiceTest/voice1-response-ok.json"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/voicesimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("VoiceTest/voice2-error-config-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("VoiceTest/voice2-error-config-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryInputValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/voicesimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("VoiceTest/voice3-error-inputs-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("VoiceTest/voice3-error-inputs-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
