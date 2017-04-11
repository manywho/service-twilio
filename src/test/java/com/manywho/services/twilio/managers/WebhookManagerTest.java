package com.manywho.services.twilio.managers;

import com.manywho.services.twilio.entities.MessageCallback;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class WebhookManagerTest {
    @Test
    public void testInternationalFormatNumber() throws Exception {
        CallbackManager cacheManager = mock(CallbackManager.class);
        WebhookManager webhookManager = new WebhookManager(cacheManager);

        MessageCallback messageCallback = mock(MessageCallback.class);
        when(messageCallback.getSmsStatus()).thenReturn("received");
        when(messageCallback.getAccountSid()).thenReturn("account-sid");
        when(messageCallback.getMessageSid()).thenReturn("message-sid");
        when(messageCallback.getBody()).thenReturn("text body");

        when(messageCallback.getFrom()).thenReturn("1234567890");
        when(messageCallback.getFromCountry()).thenReturn("GB");

        when(messageCallback.getTo()).thenReturn("5121234567");
        when(messageCallback.getToCountry()).thenReturn("US");

        webhookManager.handleMessageStatus(messageCallback);

        verify(cacheManager).processMessageReply("account-sid", "message-sid", "+441234567890",
                "+15121234567", "text body");
    }


    @Test
    public void testInternationalFormatNumberFromInternational() throws Exception {
        CallbackManager cacheManager = mock(CallbackManager.class);
        WebhookManager webhookManager = new WebhookManager(cacheManager);

        MessageCallback messageCallback = mock(MessageCallback.class);
        when(messageCallback.getSmsStatus()).thenReturn("received");
        when(messageCallback.getAccountSid()).thenReturn("account-sid");
        when(messageCallback.getMessageSid()).thenReturn("message-sid");
        when(messageCallback.getBody()).thenReturn("text body");

        when(messageCallback.getFrom()).thenReturn("+441234567890");
        when(messageCallback.getFromCountry()).thenReturn("GB");

        when(messageCallback.getTo()).thenReturn("+15121234567");
        when(messageCallback.getToCountry()).thenReturn("US");

        webhookManager.handleMessageStatus(messageCallback);

        verify(cacheManager).processMessageReply("account-sid", "message-sid", "+441234567890",
                "+15121234567", "text body");
    }
}
