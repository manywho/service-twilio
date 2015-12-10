package com.manywho.services.twilio.entities;

import com.manywho.sdk.entities.run.EngineInvokeResponse;

public class TenantInvokeResponseTuple {
    private String tenantId;
    private EngineInvokeResponse invokeResponse;

    public TenantInvokeResponseTuple() {
    }

    public TenantInvokeResponseTuple(String tenantId, EngineInvokeResponse invokeResponse) {
        this.tenantId = tenantId;
        this.invokeResponse = invokeResponse;
    }

    public String getTenantId() {
        return tenantId;
    }

    public EngineInvokeResponse getInvokeResponse() {
        return invokeResponse;
    }
}
