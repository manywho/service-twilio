package com.manywho.services.twilio.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.TenantInvokeResponseTuple;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;

public class CacheManager {
    private final static String REDIS_KEY_CALLS = "service:twilio:requests:calls:%s";
    private static final String REDIS_KEY_MESSAGES = "service:twilio:requests:message:%s:%s";
    private static final String REDIS_KEY_TWIML_APP = "service:twilio:twiml:app:%s";
    private static final String REDIS_KEY_FLOWS = "service:twilio:flows:%s:%s";
    private static final String REDIS_KEY_TRANSCRIPTIONS = "service:twilio:transcriptions:%s";

    @Inject
    private JedisPool jedisPool;

    @Inject
    private ObjectMapper objectMapper;

    public void deleteCallRequest(String sid) {
        String key = String.format(REDIS_KEY_CALLS, sid);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public String getTwimlApplication(String accountSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(REDIS_KEY_TWIML_APP, accountSid));
        }
    }

    public void setTwimlApplication(String accountSid, String applicationSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_TWIML_APP, accountSid), applicationSid);
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

    public void deleteMessageRequest(String accountSid, String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(String.format(REDIS_KEY_MESSAGES, accountSid, id));
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

    public TenantInvokeResponseTuple getFlowExecution(String stateId, String callSid) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_FLOWS, stateId, callSid));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, TenantInvokeResponseTuple.class);
            }
        }

        throw new Exception("Could not find a stored flow execution for the call with SID: " + callSid);
    }

    public void saveFlowExecution(String stateId, String callSid, TenantInvokeResponseTuple tuple) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_FLOWS, stateId, callSid), objectMapper.writeValueAsString(tuple));
        }
    }

    public boolean hasFlowExecution(String stateId, String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_FLOWS, stateId, callSid));
        }
    }

    public boolean hasTranscription(String stateId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_TRANSCRIPTIONS, stateId));
        }
    }

    public void saveTranscription(String stateId, String transcriptionText) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_TRANSCRIPTIONS, stateId), transcriptionText);
        }
    }

    public String getTranscription(String stateId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(String.format(REDIS_KEY_TRANSCRIPTIONS, stateId));
        }
    }
}
