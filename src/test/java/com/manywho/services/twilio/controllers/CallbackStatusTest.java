package com.manywho.services.twilio.controllers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.junit.Assert.assertEquals;
import com.manywho.services.test.TwilioServiceFunctionalTest;
import org.json.JSONException;
import org.junit.Test;

public class CallbackStatusTest extends TwilioServiceFunctionalTest {
    @Test
    public void testMessageCallbackStatus() throws URISyntaxException, IOException, JSONException {

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

        final Form form = new Form();
        form.param("AccountSid", "mockAppSid");
        form.param("ApiVersion", "2010-04-01");
        form.param("From", "+440123456789");
        form.param("MessageSid", "SMd931e01d8ce64158b8c962c6a1b24e5c");
        form.param("MessageStatus", "sent");
        form.param("SmsSid", "SMd931e01d8ce64158b8c962c6a1b24e5c");
        form.param("SmsStatus", "sent");
        form.param("To", "+441234567899");

        Entity entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        String redisKey =  String.format(
                "service:twilio:requests:message:%s:%s",
                "mockAppSid",
                "SMd931e01d8ce64158b8c962c6a1b24e5c"
        );

        mockJedis.set(
                redisKey,
                getJsonFormatFileContent("CallbackStatusTest/callbackstatus1-redis")
        );

        Response responseMsg = target("/callback/status/message")
                .request()
                .headers(headers)
                .post(entity);

        assertEquals(204, responseMsg.getStatus());
        assertEquals("", responseMsg.readEntity(String.class));
    }
}
