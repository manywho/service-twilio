package com.manywho.services.twilio.controllers;

import com.fiftyonred.mock_jedis.MockJedis;
import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.twilio.sdk.resource.instance.Sms;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Test;

public class SendSmsTest extends TwilioServiceFunctionalTest {
    @Test
    public void testSendSms() throws Exception {
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
        mockJedis.set("service:twilio:twiml:app:mockAppSid", "mockAppSid");

        Response responseMsg = target("/messages/sms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsTest/sms1-ok-request"));

        assertEquals(200, responseMsg.getStatus());
        assertEquals("[application/json]", responseMsg.getHeaders().get("Content-Type").toString());
        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms1-ok-response"),
                getJsonFormatResponse(responseMsg)
        );

        //check that the message have been sent to twilio
        verify(mockSmsFactory, times(1)).create(messageParameters);

        //check the message is saved in redis
        assertEquals(
                getJsonFormatFileContent("SendSmsTest/sms1-ok-request"),
                getJsonFormat(mockJedis.get("service:twilio:requests:message:mockAppSid:1"))
        );
        assertEquals(
                getJsonFormatFileContent("SendSmsTest/sms1-ok-request"),
                getJsonFormat(mockJedis.get("service:twilio:requests:message:mockAppSid:44012345678900440123456788"))
        );
    }

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/messages/sms")
                .request()
                .post(getServerRequestFromFile("SendSmsTest/sms1-ok-request"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/sms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsTest/sms2-error-config-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms2-error-config-response"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryInputValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/sms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsTest/sms3-error-inputs-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms3-error-inputs-response"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
