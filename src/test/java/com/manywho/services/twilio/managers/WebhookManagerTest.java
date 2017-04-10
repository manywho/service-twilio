package com.manywho.services.twilio.managers;

import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WebhookManagerTest {
    @Test
    public void testInternationalFormatNumber() throws NumberParseException {
        String international1 = WebhookManager.internationalFormatE164("+441234567890", "GB");
        String international2 = WebhookManager.internationalFormatE164("1234567890", "GB");

        assertEquals("+441234567890", international1);
        assertEquals("+441234567890", international2);
    }
}
