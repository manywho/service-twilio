package com.manywho.services.twilio.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

public class OutboundTest extends TwilioServiceFunctionalTest {

    @Test
    public void testCheckAuthentication() throws IOException, URISyntaxException {
        Response responseMsg = target("/calls/outbound")
                .request()
                .post(getServerRequestFromFile("OutboundTest/outbound1-request-ok"));

        assertEquals(401, responseMsg.getStatus());
    }

    @Test
    public void testMandatoryConfigurationValues() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/calls/outbound")
                .request()
                .headers(headers)
                .post(getServerRequestFromFile("OutboundTest/outbound2-error-config-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("OutboundTest/outbound2-error-config-response"),
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
                .post(getServerRequestFromFile("OutboundTest/outbound3-error-inputs-request"));

        assertEquals(400, responseMsg.getStatus());
        assertJsonSame(
                getJsonFormatFileContent("OutboundTest/outbound3-error-inputs-response"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
