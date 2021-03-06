package com.manywho.services.twilio.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.twilio.sdk.resource.instance.Message;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

public class SendSmsTest extends TwilioServiceFunctionalTest {
    @Test
    public void testSendSms() throws Exception {
        when(mockTwilioClientFactory.createTwilioRestClient("mockAppSid","mockAuthToken"))
                .thenReturn(mockTwilioRestClient);

        final List<NameValuePair> messageParameters = new ArrayList<>();
        messageParameters.add(new BasicNameValuePair("To", "00440123456788"));
        messageParameters.add(new BasicNameValuePair("From", "440123456789"));
        messageParameters.add(new BasicNameValuePair("Body", "hello message"));
        messageParameters.add(new BasicNameValuePair("StatusCallback", "http://localhost:9998/callback/status/message"));

        Message sms = mock(Message.class);
        when(sms.getAccountSid()).thenReturn("mockAppSid");
        when(sms.getSid()).thenReturn("1");
        when(sms.getFrom()).thenReturn("440123456789");
        when(sms.getTo()).thenReturn("00440123456788");
        when(mockMessageFactory.create(messageParameters)).thenReturn(sms);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        // save fake application sid in cache
        mockJedis.set("service:twilio:callbackTwiml:app:mockAppSid", "mockAppSid");

        Response responseMsg = target("/messages/sms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsTest/sms1-ok-request.json"));

        assertEquals(200, responseMsg.getStatus());
        assertEquals("[application/json]", responseMsg.getHeaders().get("Content-Type").toString());
        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms1-ok-response.json"),
                getJsonFormatResponse(responseMsg)
        );

        //check that the message have been sent to twilio
        verify(mockMessageFactory, times(1)).create(messageParameters);

        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms1-ok-request.json"),
                mockJedis.get("service:twilio:requests:message:mockAppSid:44012345678900440123456788")
        );
        String authenticatedWhoJson = new ObjectMapper().writeValueAsString(getDefaultAuthenticatedWho()); 
        assertJsonSame(
                authenticatedWhoJson,
                mockJedis.get("service:twilio:requests:who:mockAppSid:44012345678900440123456788")
        );
    }

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/messages/sms")
                .request()
                .post(getServerRequestFromFile("SendSmsTest/sms1-ok-request.json"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/sms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendSmsTest/sms2-error-config-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms2-error-config-response.json"),
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
                .post(getServerRequestFromFile("SendSmsTest/sms3-error-inputs-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendSmsTest/sms3-error-inputs-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
