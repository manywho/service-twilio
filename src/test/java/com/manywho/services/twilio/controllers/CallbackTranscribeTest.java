package com.manywho.services.twilio.controllers;

import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.manywho.services.twilio.managers.CacheManager;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class CallbackTranscribeTest extends TwilioServiceFunctionalTest{
    @Test
    public void testCallbackTranscribe() throws IOException, URISyntaxException {
        final Form form = new Form();
        form.param("TranscriptionText", "Hello World!");
        form.param("RecordingUrl", "https://api.twilio.com/2010-04-01/Accounts/AC1234/Recordings/RE1234");
        form.param("CallSid", "12345");
        form.param("TranscriptionService", "completed");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        Response responseMsg = target("/callback/transcribe/123456789")
                .request()
                .headers(headers)
                .post(entity);

        assertEquals(204, responseMsg.getStatus());

        assertJsonSame(
                mockJedis.get(String.format(CacheManager.REDIS_KEY_RECORDINGS, "123456789", "12345")),
                getJsonFormatFileContent("CallbackTranscribeTest/cache/transcription-to-save")
        );
    }
}
