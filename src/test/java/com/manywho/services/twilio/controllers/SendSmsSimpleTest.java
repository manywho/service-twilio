package com.manywho.services.twilio.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import javax.ws.rs.core.*;
import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.twilio.sdk.resource.instance.Sms;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class SendSmsSimpleTest extends TwilioServiceFunctionalTest {

    @Test
    public void testSendSimpleMessage() throws Exception {
        when(mockTwilioClientFactory.createTwilioRestClient("mockAppSid","mockAuthToken"))
                .thenReturn(mockTwilioRestClient);

        final Map<String, String> messageParameters = new HashMap<>();
        messageParameters.put("From", "440123456789");
        messageParameters.put("To", "00440123456788");
        messageParameters.put("Body", "hello message");
        messageParameters.put("StatusCallback", "http://localhost:9998/callback/status/message");

        Sms sms = mock(Sms.class);
        when(sms.getAccountSid()).thenReturn("mockAppSid");
        when(sms.getSid()).thenReturn("1");
        when(sms.getFrom()).thenReturn("440123456789");
        when(sms.getTo()).thenReturn("00440123456788");

        when(mockSmsFactory.create(messageParameters)).thenReturn(sms);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        // save fake application sid in cache
        mockJedis.set("service:twilio:callbackTwiml:app:mockAppSid", "mockAppSid");

        Response responseMsg = target("/messages/smssimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsSimpleTest/smssimple1-ok-request.json"));

        assertEquals(200, responseMsg.getStatus());
        assertEquals("[application/json]", responseMsg.getHeaders().get("Content-Type").toString());
        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendSmsSimpleTest/smssimple1-ok-response.json"),
                getJsonFormatResponse(responseMsg)
        );
        //check that the message have been sent to twilio
        verify(mockSmsFactory, times(1)).create(messageParameters);
        //check the message is saved in redis
        assertJsonSame(
                getJsonFormatFileContent("SendSmsSimpleTest/smssimple1-ok-request.json"),
                mockJedis.get("service:twilio:requests:message:mockAppSid:1")
        );
        assertJsonSame(
                getJsonFormatFileContent("SendSmsSimpleTest/smssimple1-ok-request.json"),
                mockJedis.get("service:twilio:requests:message:mockAppSid:44012345678900440123456788")
        );
    }

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/messages/smssimple")
                .request()
                .post(getServerRequestFromFile("SendSmsSimpleTest/smssimple1-ok-request.json"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/smssimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsSimpleTest/smssimple2-error-config-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsSimpleTest/smssimple2-error-config-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryInputValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/smssimple")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsSimpleTest/smssimple3-error-inputs-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsSimpleTest/smssimple3-error-inputs-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
