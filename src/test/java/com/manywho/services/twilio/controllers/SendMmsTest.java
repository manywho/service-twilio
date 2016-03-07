package com.manywho.services.twilio.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MediaList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Test;

public class SendMmsTest extends TwilioServiceFunctionalTest {
    @Test
    public void testSendMms() throws Exception {
        // a new twilio client is created whit the credentials passed throw the api
        when(mockTwilioClientFactory.createTwilioRestClient("mockAppSid","mockAuthToken"))
                .thenReturn(mockTwilioRestClient);

        MediaList mediaList = new MediaList(mockTwilioRestClient);

        Message mms = mock(Message.class);
        when(mms.getAccountSid()).thenReturn("mockAppSid");
        when(mms.getSid()).thenReturn("1");
        when(mms.getFrom()).thenReturn("440123456789");
        when(mms.getTo()).thenReturn("00440123456788");
        when(mms.getMedia()).thenReturn(mediaList);

        final List<NameValuePair> messageParameters = new ArrayList<>();
        messageParameters.add(new BasicNameValuePair("To", "00440123456788"));
        messageParameters.add(new BasicNameValuePair("From", "440123456789"));
        messageParameters.add(new BasicNameValuePair("Body", "hello message"));
        messageParameters.add(new BasicNameValuePair("StatusCallback", "http://localhost:9998/callback/status/message"));
        messageParameters.add(new BasicNameValuePair("MediaUrl", "https://demo.twilio.com/owl.png"));

        when(mockMessageFactory.create(messageParameters)).thenReturn(mms);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        // save fake application sid in cache
        mockJedis.set("service:twilio:callbackTwiml:app:mockAppSid", "mockAppSid");

        Response responseMsg = target("/messages/mms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendMmsTest/mms1-ok-request.json"));

        assertEquals(200, responseMsg.getStatus());
        assertEquals("[application/json]", responseMsg.getHeaders().get("Content-Type").toString());
        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendMmsTest/mms1-ok-response.json"),
                getJsonFormatResponse(responseMsg)
        );
        //check that the message have been sent to twilio
        verify(mockMessageFactory, times(1)).create(messageParameters);
        //check the message is saved in redis
        assertJsonSame(
                getJsonFormatFileContent("SendMmsTest/mms1-ok-request.json"),
                mockJedis.get("service:twilio:requests:message:mockAppSid:1")
        );
        assertJsonSame(
                getJsonFormatFileContent("SendMmsTest/mms1-ok-request.json"),
                mockJedis.get("service:twilio:requests:message:mockAppSid:44012345678900440123456788")
        );
    }

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/messages/mms")
                .request()
                .post(getServerRequestFromFile("SendMmsTest/mms1-ok-request.json"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/mms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendMmsTest/mms2-error-config-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendMmsTest/mms2-error-config-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }

    @Test
    public void testMandatoryInputValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/messages/mms")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("SendMmsTest/mms3-error-inputs-request.json"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("SendMmsTest/mms3-error-inputs-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
