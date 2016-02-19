package com.manywho.services.twilio.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class CallRecording extends AbstractType {
    public final static String NAME = "Call Recording";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        return new TypeElementBindingCollection() {{
                add(new TypeElementBinding(NAME, "Call Recording", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Recording Url", "Recording Url"));
            }}));
        }};
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Recording Url", ContentType.String));
        }};
    }
}
