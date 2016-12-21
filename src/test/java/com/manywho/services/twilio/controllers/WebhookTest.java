package com.manywho.services.twilio.controllers;

import com.manywho.services.test.FlowResponseMock;
import com.manywho.services.test.HttpClientForTest;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.manywho.services.twilio.managers.CacheManager;
import com.mashape.unirest.http.Unirest;
import org.json.JSONException;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WebhookTest extends TwilioServiceFunctionalTest{
    @Test
    public void testMessageCallbackStatusWhenThereIsFlowWaitting() throws URISyntaxException, IOException, JSONException {

        MultivaluedMap<String,Object> headers = defaultHeadersFromTwilio();

        final Form form = new Form();
        form.param("AccountSid", "mockAppSid");
        form.param("ApiVersion", "2010-04-01");
        form.param("From", "+440123456789");
        form.param("MessageSid", "SMd931e01d8ce64158b8c962c6a1b24e5c");
        form.param("MessageStatus", "sent");
        form.param("SmsSid", "SMd931e01d8ce64158b8c962c6a1b24e5c");
        form.param("SmsStatus", "sent");
        form.param("To", "+441234567899");

        HttpClientForTest httpClientMock = new HttpClientForTest();
        Unirest.setHttpClient(httpClientMock);

        // I will do to calls to the flow to know the status
        FlowResponseMock httpResponse = new FlowResponseMock();
        httpClientMock.addResponse(httpResponse);

        FlowResponseMock httpResponse1 = new FlowResponseMock();
        httpClientMock.addResponse(httpResponse1);

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        String redisKey =  String.format(
                CacheManager.REDIS_KEY_MESSAGES,
                "mockAppSid",
                "SMd931e01d8ce64158b8c962c6a1b24e5c"
        );

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("WebhookTest/callbackstatus1-redis.json")
        );

        mockJedis.set(String.format(CacheManager.REDIS_KEY_FLOW_WAITTING_SMS_REPLAY, "+441234567899+440123456789"), "true");

        // this call is coming from Twilio
        Response responseMsg = target("/callback/sms/flow/3525b86c-c780-11e6-9d9d-cec0c932ce01/d2744eee-c780-11e6-9d9d-cec0c932ce01")
                .request()
                .headers(headers)
                .post(entity);

        assertJsonSame(
                getJsonFormatFileContent("WebhookTest/MessageCallbackStatus/forward-flow-call.json"),
                httpClientMock.getExpectedRequestBody(0)
        );

        assertJsonSame(
                getJsonFormatFileContent("WebhookTest/MessageCallbackStatus/wait-message-flow-call.json"),
                httpClientMock.getExpectedRequestBody(1)
        );

        assertNull(mockJedis.get(String.format(CacheManager.REDIS_KEY_FLOW_WAITTING_SMS_REPLAY, "+441234567899+440123456789")));

        // headers used to call the flow in first call
        checkHeaders(httpClientMock, 0);
        checkHeaders(httpClientMock, 1);

        assertEquals(2, httpClientMock.getResponsesHistory().size());
        assertEquals(204, responseMsg.getStatus());
        assertEquals("", responseMsg.readEntity(String.class));
    }

    private void checkHeaders(HttpClientForTest httpClientMock, Integer index) {
        // headers used to call the flow
        assertEquals(null, httpClientMock.getExpectedRequestHeader(index, "Authorization").getValue());
        assertEquals("3525b86c-c780-11e6-9d9d-cec0c932ce01", httpClientMock.getExpectedRequestHeader(index, "ManyWhoTenant").getValue());
        assertEquals("gzip", httpClientMock.getExpectedRequestHeader(index, "accept-encoding").getValue());
        assertEquals("application/json", httpClientMock.getExpectedRequestHeader(index, "Content-Type").getValue());
    }

    private MultivaluedMap<String,Object> defaultHeadersFromTwilio() {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip,deflate");
        headers.add("Cache-Control", "max-age=259200");
        headers.add("Connection", "close");
        headers.add("Content-Length", "229");
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("User-Agent", "TwilioProxy/1.1");
        headers.add("X-Forwarded-For", "54.165.71.167");
        headers.add("X-Forwarded-Proto", "https");
        headers.add("X-Twilio-Signature","yNFtLun3RciDiywtxuGTZSTRkoY=");

        return headers;
    }
}
