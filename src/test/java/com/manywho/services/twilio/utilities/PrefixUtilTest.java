package com.manywho.services.twilio.utilities;

import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrefixUtilTest {
    @Test
    public void testInternationalFormatNumber() throws NumberParseException {
        String international1 = PrefixUtil.internationalFormatE164("+441234567890", "GB");
        String international2 = PrefixUtil.internationalFormatE164("1234567890", "GB");

        assertEquals("+441234567890", international1);
        assertEquals("+441234567890", international2);
    }
}
