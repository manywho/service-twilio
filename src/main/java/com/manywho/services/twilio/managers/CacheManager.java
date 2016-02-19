package com.manywho.services.twilio.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.client.RunClient;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.entities.TenantInvokeResponseTuple;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class CacheManager {
    public final static String REDIS_KEY_CALLS = "service:twilio:requests:calls:%s";
    public static final String REDIS_KEY_MESSAGES = "service:twilio:requests:message:%s:%s";
    public static final String REDIS_KEY_FLOWS = "service:twilio:flows:%s:%s";
    public static final String REDIS_KEY_RECORDINGS = "service:twilio:recordings:%s:%s";
    public static final String REDIS_KEY_CALL_RECORDINGS = "service:twilio:recordings:call:%s";
    public static final String REDIS_KEY_TWIML_HANGUP_CALL = "service:twilio:callbackTwiml:hangup:call:%s";

    @Inject
    private JedisPool jedisPool;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private RunClient runClient;

    public void deleteCallRequest(String sid) {
        String key = String.format(REDIS_KEY_CALLS, sid);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public ServiceRequest getCallRequest(String sid) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_CALLS, sid));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, ServiceRequest.class);
            }
        }

        throw new Exception("Could not find a stored request for the call with SID: " + sid);
    }

    public void saveCallRequest(String sid, ServiceRequest serviceRequest) throws Exception {
        String key = String.format(REDIS_KEY_CALLS, sid);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(serviceRequest));
        }
    }

    public boolean hasCallRequest(String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_CALLS, callSid));
        }
    }

    public ServiceRequest getMessageRequest(String accountSid, String id) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_MESSAGES, accountSid, id));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, ServiceRequest.class);
            }
        }

        throw new Exception("Could not find a stored request for the message with SID or From number: " + id);
    }

    public void saveMessageRequest(String accountSid, String id, String request) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_MESSAGES, accountSid, id), request);
        }
    }

    public FlowState getFlowExecution(String stateId, String callSid) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_FLOWS, stateId, callSid));

            if (StringUtils.isNotEmpty(json)) {
                TenantInvokeResponseTuple tuple = objectMapper.readValue(json, TenantInvokeResponseTuple.class);

                return new FlowState(runClient, tuple.getTenantId(), tuple.getInvokeResponse());
            }
        }

        throw new Exception("Could not find a stored flow execution for the call with SID: " + callSid);
    }

    public void saveFlowExecution(@Nonnull String stateId, @Nonnull String callSid, @Nonnull FlowState flowState) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_FLOWS, stateId, callSid), objectMapper.writeValueAsString(new TenantInvokeResponseTuple(flowState.getTenantId(), flowState.getInvokeResponse())));
        }
    }

    public boolean hasFlowExecution(String stateId, String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_FLOWS, stateId, callSid));
        }
    }

    public boolean hasRecordingCallback(String stateId, String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_RECORDINGS, stateId, callSid));
        }
    }

    public void saveRecordingCallback(String stateId, String callSid, RecordingCallback recordingCallback) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_RECORDINGS, stateId, callSid), objectMapper.writeValueAsString(recordingCallback));
        }
    }

    public RecordingCallback getRecordingCallback(String stateId, String callSid) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_RECORDINGS, stateId, callSid));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, RecordingCallback.class);
            }
        }

        throw new Exception("Could not find a Recording for the call with SID: " + callSid);
    }

    public void deleteRecordingCallback(String stateId, String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(String.format(REDIS_KEY_RECORDINGS, stateId, callSid));
        }
    }

    public void saveCallRecordingSid (String callSid, String recordSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_CALL_RECORDINGS, callSid), recordSid);
        }
    }

    public String getCallRecordingSid (String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(REDIS_KEY_CALL_RECORDINGS, callSid));
        }
    }

    public void saveCallHungupByTwiml (String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_TWIML_HANGUP_CALL, callSid), callSid);
        }
    }

    public boolean isCallHungupByTwiml(String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_TWIML_HANGUP_CALL, callSid, callSid));
        }
    }

}
