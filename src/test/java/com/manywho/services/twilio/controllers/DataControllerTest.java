package com.manywho.services.twilio.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import com.manywho.services.twilio.managers.CacheManager;
import org.junit.Test;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;

public class DataControllerTest extends TwilioServiceFunctionalTest {

    @Test
    public void testLoadRecordingCallUrl() throws Exception {
        mockJedis.set(String.format(CacheManager.REDIS_KEY_CALL_RECORDINGS, "CA1234"), "RE1234");

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/data")
                .request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("DataLoadTest/request.json"));

        assertEquals(200, responseMsg.getStatus());
        assertEquals("[application/json]", responseMsg.getHeaders().get("Content-Type").toString());
        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("DataLoadTest/response"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
