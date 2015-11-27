package com.manywho.services.twilio.controllers;

import com.manywho.services.test.TwilioServiceFunctionalTest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.junit.Assert.assertEquals;
import org.json.JSONException;
import org.junit.Test;

public class CallbackTwimlMessageTest extends TwilioServiceFunctionalTest {
    @Test
    public void testCallbackTwimlMessage() throws URISyntaxException, IOException, JSONException {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip,deflate");
        headers.add("Cache-Control", "max-age=259200");
        headers.add("Connection", "close");
        headers.add("Content-Length", "381");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("User-Agent", "TwilioProxy/1.1");
        headers.add("X-Forwarded-For", "54.172.191.132");
        headers.add("X-Forwarded-Proto", "https");
        headers.add("X-Twilio-Signature","7cdSKsktZUtYRwqCY289QTuurII=");

        final Form form = new Form();
        form.param("AccountSid", "mockAppSid");
        form.param("ApiVersion", "2010-04-01");
        form.param("Body","Hi");
        form.param("From", "+440123456789");
        form.param("FromCity","");
        form.param("FromCountry","GB");
        form.param("FromState","");
        form.param("FromZip","");
        form.param("MessageSid", "SMd931e01d8ce64158b8c962c6a1b24e5c");
        form.param("NumMedia","0");
        form.param("NumSegments","1");
        form.param("SmsMessageSid","SM16a2ee26ba827371462e6de51c2cdca6");
        form.param("SmsSid", "SM16a2ee26ba827371462e6de51c2cdca6");
        form.param("SmsStatus", "received");
        form.param("To", "+440123456789");
        form.param("ToCity","");
        form.param("ToCountry","GB");
        form.param("ToState","Ely");
        form.param("ToZip","");
        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        String redisKey =  String.format(
                "service:twilio:requests:message:%s:%s",
                "mockAppSid",
                "+440123456789+440123456789"
        );

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("CallbackStatusTest/callbackstatus1-redis")
        );

        Response responseMsg = target("/callback/twiml/message")
                .request()
                .headers(headers)
                .post(entity);

        assertEquals(204, responseMsg.getStatus());
        assertEquals("", responseMsg.readEntity(String.class));
    }
}
