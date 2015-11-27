package com.manywho.services.twilio.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.twilio.types.Sms;

public class SendSms extends AbstractAction {
    @Override
    public String getUriPart() {
        return "messages/sms";
    }

    @Override
    public String getDeveloperName() {
        return "Send SMS";
    }

    @Override
    public String getDeveloperSummary() {
        return "Send an SMS message to a phone number";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Message", ContentType.Object, true, null, Sms.NAME));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Reply", ContentType.Object, false, null, Sms.NAME));
        }};
    }
}
