package com.manywho.services.twilio.utilities;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PrefixUtil {

    public static String internationalFormatE164(String number, String country) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, country);

        return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
