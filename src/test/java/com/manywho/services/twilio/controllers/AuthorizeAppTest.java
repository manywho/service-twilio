package com.manywho.services.twilio.controllers;

import javax.ws.rs.core.*;
import static org.junit.Assert.assertEquals;

import com.manywho.services.test.TwilioServiceFunctionalTest;
import org.junit.Test;

public class AuthorizeAppTest extends TwilioServiceFunctionalTest {
    @Test
    public void testAuthorizedApp() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.add("Accept-Encoding", "gzip, deflate, sdch");
        headers.add("Accept-Language","en-GB,en-US;q=0.8,en;q=0.6");
        headers.add("Upgrade-Insecure-Requests", "1");
        headers.add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
        headers.add("X-Forwarded-For", "213.123.137.96");
        headers.add("X-Forwarded-Proto", "https");

        Response responseMsg = target("/authorized/app")
                .queryParam("AccountSid","abc1234")
                .request()
                .headers(headers)
                .get();

        assertEquals("abc1234", responseMsg.readEntity(String.class));

    }
}
