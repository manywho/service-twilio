package com.manywho.services.twilio.controllers.callbackTwiml;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.FlowResponseMock;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.manywho.services.twilio.managers.CacheManager;
import org.junit.Test;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import static org.junit.Assert.assertEquals;

public class VoiceFlowStateCallback extends TwilioServiceFunctionalTest {

    @Test
    public void testVoiceFlowCallback() throws Exception {

        final Form form = new Form();
        form.param("CallSid", "CA12345");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        FlowResponseMock httpResponse = new FlowResponseMock(
                FlowResponseMock.getFullListHeaders(), "HTTP", 1, 1, 200, "ok","Content-Type: application/json; charset=utf-8",
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/VoiceFlowState/flow/join-flow-response.json")
        );

        mockHttpClient.addResponse(httpResponse);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        String redisKey =  String.format(CacheManager.REDIS_KEY_CALLS, "CA12345");

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/VoiceFlowState/cache/outbound-request.json")
        );

        Response responseMsg = target("/callback/callbackTwiml/voice/flow/state/12345678")
                .request()
                .headers(headers)
                .post(entity);

        // check the flow have been saved in redis
        assertJsonSame(
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/VoiceFlowState/cache/flow-state-saved.json"),
                mockJedis.get(String.format(CacheManager.REDIS_KEY_FLOWS, "5f942f66-7840-4e4d-8209-09647fc67261", "CA12345"))
        );

        assertEquals(1, mockHttpClient.getResponsesHistory().size());
        assertEquals(200, responseMsg.getStatus());

        // the order in this xml is important
        assertXMLEqual(
                "The XML response is not the expected.",
                getFileContent("CallbackTwiml/VoiceFlowStateCallback/VoiceFlowState/response.xml"),
                responseMsg.readEntity(String.class)
        );
    }

    @Test
    public void testVoiceFlowCallbackWaitingForTranscription() throws Exception {
        final Form form = new Form();
        form.param("CallSid", "CA12345");
        form.param("Digits", "hangup");
        form.param("RecordingUrl", "https://api.twilio.com/2010-04-01/Accounts/AC123/Recordings/REb123");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Referer", "http://localhost/api/twilio/2/callback/callbackTwiml/voice/flow/state/12345678");
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        String redisKey =  String.format(CacheManager.REDIS_KEY_FLOWS, "123456", "CA12345");

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/WaitingForTranscription/cache/executed-flow.json")
        );

        Response responseMsg = target("/callback/callbackTwiml/voice/flow/state/123456")
                .request()
                .headers(headers)
                .post(entity);

        assertEquals(200, responseMsg.getStatus());

        // the order in this xml is important
        assertXMLEqual(
                "The XML response is not the expected.",
                getFileContent("CallbackTwiml/VoiceFlowStateCallback/WaitingForTranscription/response.xml"),
                responseMsg.readEntity(String.class)
        );
    }

    @Test
    public void testVoiceFlowCallbackWaitingForStep() throws Exception {
        final Form form = new Form();
        form.param("CallSid", "CA12345");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        FlowResponseMock httpResponse = new FlowResponseMock(
                FlowResponseMock.getFullListHeaders(), "HTTP", 1, 1, 200, "ok","Content-Type: application/json; charset=utf-8",
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/WaitingForStep/flow/sync-response.json")
        );

        mockHttpClient.addResponse(httpResponse);


        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Referer", "http://localhost/api/twilio/2/callback/callbackTwiml/voice/flow/state/12345678");
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        String redisKey =  String.format(CacheManager.REDIS_KEY_FLOWS, "123456", "CA12345");

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/WaitingForStep/cache/executed-flow.json")
        );

        Response responseMsg = target("/callback/callbackTwiml/voice/flow/state/123456")
                .request()
                .headers(headers)
                .post(entity);

        assertEquals(200, responseMsg.getStatus());

        // the order in this xml is important
        assertXMLEqual(
                "The XML response is not the expected.",
                getFileContent("CallbackTwiml/VoiceFlowStateCallback/WaitingForStep/response.xml"),
                responseMsg.readEntity(String.class)
        );
    }

    @Test
    public void testVoiceFlowCallbackRecordingReady() throws Exception {
        final Form form = new Form();
        form.param("CallSid", "CA12345");
        form.param("Digits", "hangup");
        form.param("RecordingUrl", "");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Referer", "http://localhost/api/twilio/2/callback/callbackTwiml/voice/flow/state/12345678");
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        mockJedis.set(
                String.format(CacheManager.REDIS_KEY_FLOWS, "123456", "CA12345"),
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/RecordingReady/cache/executed-flow.json")
        );

        mockJedis.set(
                String.format(CacheManager.REDIS_KEY_RECORDINGS, "123456", "CA12345"),
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/RecordingReady/cache/recording-call.json")
        );

        FlowResponseMock httpResponse = new FlowResponseMock(
                FlowResponseMock.getFullListHeaders(), "HTTP", 1, 1, 200, "ok","Content-Type: application/json; charset=utf-8",
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/RecordingReady/flow/run-to-next-step.json")
        );

        mockHttpClient.addResponse(httpResponse);

        Response responseMsg = target("/callback/callbackTwiml/voice/flow/state/123456")
                .request()
                .headers(headers)
                .post(entity);

        assertJsonSame(
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/RecordingReady/cache/saved-flow-execution.json"),
                mockJedis.get(String.format(CacheManager.REDIS_KEY_FLOWS, "5f942f66-7840-4e4d-8209-09647fc67261", "CA12345"))
        );

        assertEquals(200, responseMsg.getStatus());

        // the order in this xml is important
        assertXMLEqual(
                "The XML response is not the expected.",
                getFileContent("CallbackTwiml/VoiceFlowStateCallback/RecordingReady/response.xml"),
                responseMsg.readEntity(String.class)
        );
    }

    @Test
    public void testVoiceFlowCallbackTranscriptionFailed() throws Exception {
        final Form form = new Form();
        form.param("CallSid", "CA12345");
        form.param("Digits", "hangup");
        form.param("RecordingUrl", "");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Referer", "http://localhost/api/twilio/2/callback/callbackTwiml/voice/flow/state/12345678");
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        mockJedis.set(
                String.format(CacheManager.REDIS_KEY_FLOWS, "123456", "CA12345"),
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/TranscriptionFail/flow/executed-flow.json")
        );

        mockJedis.set(
                String.format(CacheManager.REDIS_KEY_RECORDINGS, "123456", "CA12345"),
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/TranscriptionFail/cache/recording-call.json")
        );

        FlowResponseMock httpResponse = new FlowResponseMock(
                FlowResponseMock.getFullListHeaders(), "HTTP", 1, 1, 200, "ok","Content-Type: application/json; charset=utf-8",
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/TranscriptionFail/flow/run-to-next-step.json")
        );

        mockHttpClient.addResponse(httpResponse);

        Response responseMsg = target("/callback/callbackTwiml/voice/flow/state/123456")
                .request()
                .headers(headers)
                .post(entity);

        assertJsonSame(
                getJsonFormatFileContent("CallbackTwiml/VoiceFlowStateCallback/TranscriptionFail/cache/saved-flow-execution.json"),
                mockJedis.get(String.format(CacheManager.REDIS_KEY_FLOWS, "5f942f66-7840-4e4d-8209-09647fc67261", "CA12345"))
        );

        //check deleting recording callback mockJedi

        assertEquals(200, responseMsg.getStatus());

        // the order in this xml is important
        assertXMLEqual(
                "The XML response is not the expected.",
                getFileContent("CallbackTwiml/VoiceFlowStateCallback/TranscriptionFail/response.xml"),
                responseMsg.readEntity(String.class)
        );
    }
}
