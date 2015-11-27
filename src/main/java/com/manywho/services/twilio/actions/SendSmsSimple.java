package com.manywho.services.twilio.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;

public class SendSmsSimple extends AbstractAction {
    @Override
    public String getUriPart() {
        return "messages/smssimple";
    }

    @Override
    public String getDeveloperName() {
        return "Send SMS (Simple)";
    }

    @Override
    public String getDeveloperSummary() {
        return "Send an SMS message to a phone number without using an Object";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("From", ContentType.String));
            add(new DescribeValue("To", ContentType.String));
            add(new DescribeValue("Body", ContentType.String));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Reply", ContentType.String));
        }};
    }
}
