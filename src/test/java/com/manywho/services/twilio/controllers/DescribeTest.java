package com.manywho.services.twilio.controllers;

import com.manywho.services.test.TwilioServiceFunctionalTest;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DescribeTest extends TwilioServiceFunctionalTest {
    @Test
    public void testDescribeServiceResponse() throws Exception {
        Response responseMsg = target("/metadata").request()
                .post(getServerRequestFromFile("DescribeTest/metadata1-request"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("DescribeTest/metadata1-response"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
