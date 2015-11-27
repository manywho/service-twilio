package com.manywho.services.twilio.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.twilio.types.Call;

public class StartOutboundCall extends AbstractAction {
    @Override
    public String getUriPart() {
        return "calls/outbound";
    }

    @Override
    public String getDeveloperName() {
        return "Start Outbound Call";
    }

    @Override
    public String getDeveloperSummary() {
        return "Start an outbound phone call";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Call", ContentType.Object, true, null, Call.NAME));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return null;
    }
}
