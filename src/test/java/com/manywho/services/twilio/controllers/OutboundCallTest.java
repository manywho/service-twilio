package com.manywho.services.twilio.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
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

import com.manywho.services.twilio.managers.CacheManager;
import com.twilio.sdk.resource.instance.Call;
import org.junit.Test;

public class OutboundCallTest extends TwilioServiceFunctionalTest {

    @Test
    public void testStartOutboundSimpleCall() throws Exception {
        when(mockTwilioClientFactory.createTwilioRestClient("mockAppSid","mockAuthToken"))
                .thenReturn(mockTwilioRestClient);

        final Map<String, String> callParameters = new HashMap<>();

        callParameters.put("To", "+441234567899");
        callParameters.put("From", "+440123456789");
        callParameters.put("Timeout", "60");
        callParameters.put("IfMachine", "Hangup");
        callParameters.put("Record", "true");
        callParameters.put("StatusCallback", "http://localhost:9998/callback/status/voice");
        callParameters.put("Url", "http://localhost:9998/callback/callbackTwiml/voice/flow/state/da80bb94-7e31-42e7-bfcb-4783f46c7d65");
        Call mockCall = mock(Call.class);

        when(mockCall.getSid()).thenReturn("1234");
        when(mockCallFactory.create(callParameters)).thenReturn(mockCall);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/outboundsimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("OutboundCallTest/outbound1-request-ok"));

        assertJsonSame(
                getJsonFormatFileContent("OutboundCallTest/outbound1-response-ok"),
                getJsonFormatResponse(responseMsg)
        );

        assertJsonSame(
                getJsonFormatFileContent("OutboundCallTest/outbound1-request-ok"),
                mockJedis.get(String.format(CacheManager.REDIS_KEY_CALLS, "1234"))
        );
    }

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/calls/outbound")
                .request()
                .post(getServerRequestFromFile("OutboundCallTest/outbound1-request-ok"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/outbound")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("OutboundCallTest/outbound2-error-config-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("OutboundCallTest/outbound2-error-config-response"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryInputValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/outbound")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("OutboundCallTest/outbound3-error-inputs-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("OutboundCallTest/outbound3-error-inputs-response"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
