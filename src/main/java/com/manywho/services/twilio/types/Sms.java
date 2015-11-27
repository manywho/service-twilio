package com.manywho.services.twilio.types;

import com.manywho.sdk.entities.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Sms extends AbstractType {
    public final static String NAME = "SMS";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("To", ContentType.String));
            add(new TypeElementProperty("From", ContentType.String));
            add(new TypeElementProperty("Body", ContentType.String));
        }};
    }
}
