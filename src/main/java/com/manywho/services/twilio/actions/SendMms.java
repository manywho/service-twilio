package com.manywho.services.twilio.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.twilio.types.Mms;

public class SendMms extends AbstractAction {
    @Override
    public String getUriPart() {
        return "messages/mms";
    }

    @Override
    public String getDeveloperName() {
        return "Send MMS";
    }

    @Override
    public String getDeveloperSummary() {
        return "Send an MMS message to a phone number";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Message", ContentType.Object, true, null, Mms.NAME));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Reply", ContentType.Object, false, null, Mms.NAME));
        }};
    }
}
