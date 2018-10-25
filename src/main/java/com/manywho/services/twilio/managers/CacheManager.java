package com.manywho.services.twilio.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.retry.*;
import com.manywho.sdk.client.entities.FlowState;
import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.services.twilio.entities.MessageCallback;
import com.manywho.services.twilio.entities.RecordingCallback;
import com.manywho.services.twilio.entities.TenantInvokeResponseTuple;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheManager {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    public final static String REDIS_KEY_CALLS = "service:twilio:requests:calls:%s";
    public static final String REDIS_KEY_MESSAGES = "service:twilio:requests:message:%s:%s";
    public static final String REDIS_KEY_FLOWS = "service:twilio:flows:%s:%s";
    public static final String REDIS_KEY_RECORDINGS = "service:twilio:recordings:%s:%s";
    public static final String REDIS_KEY_CALL_RECORDINGS = "service:twilio:recordings:call:%s";
    public static final String REDIS_KEY_TWIML_HANGUP_CALL = "service:twilio:callbackTwiml:hangup:call:%s";
    public static final String REDIS_KEY_WEBHOOK_SMS = "service:twilio:webhook:sms:%s";
    public static final String REDIS_KEY_FLOW_WAITING_SMS_REPLY = "service:twilio:flow:sms:waiting:reply:%s";
    public static final String REDIS_KEY_SIMPLE_CALL = "service:twilio:flow:call:simple:%s";


    @Inject
    private JedisPool jedisPool;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private RawRunClient runClient;

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
            String key = String.format(REDIS_KEY_MESSAGES, accountSid, id);
            String json = getWithRetry(jedis, key);

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, ServiceRequest.class);
            }

            // When the service return 500 Twilio will call again, but this should't happen, the entry should always exist in redis when the reply is sent
            LOGGER.error("Message request with key {} not found in Redis", key);
        }

        throw new Exception("Could not find a stored request for the message with SID or From number: " + id);
    }

    private String getWithRetry(Jedis jedis, String key) throws InterruptedException, ExecutionException, RetryException {
        Callable<String> executeCallable = () -> this.executeCallback(jedis, key);

        // If the key is not in DB yet, then retry 13 times
        // it will wait with exponential values with a limit of 4000: 2, 4, .. 2024, 4000, 4000, ...
        return RetryerBuilder.<String>newBuilder()
                .retryIfResult(Objects::isNull)
                .withWaitStrategy(WaitStrategies.exponentialWait(2, 4000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(13))
                .build()
                .call(executeCallable);
    }

    private String executeCallback(Jedis jedis, String key) throws Exception {
        return jedis.get(key);
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

                return new FlowState(runClient, UUID.fromString(tuple.getTenantId()), tuple.getInvokeResponse());
            }
        }

        throw new Exception("Could not find a stored flow execution for the call with SID: " + callSid);
    }

    public void saveFlowExecution(@Nonnull String stateId, @Nonnull String callSid, @Nonnull FlowState flowState) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_FLOWS, stateId, callSid), objectMapper.writeValueAsString(new TenantInvokeResponseTuple(flowState.getTenant().toString(), flowState.getInvokeResponse())));
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

    public void saveSmsWebhook(MessageCallback smsWebhook) {
        String key = String.format(REDIS_KEY_WEBHOOK_SMS, smsWebhook.getMessageSid());

        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.set(key, objectMapper.writeValueAsString(smsWebhook));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MessageCallback getSmsWebhook(String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_WEBHOOK_SMS, id));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, MessageCallback.class);
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Could not find a stored request for the message with SID" + id);

    }

    public void stateWaitingForSms(String smsIdentity, String stateId) {
        String key = String.format(REDIS_KEY_FLOW_WAITING_SMS_REPLY, smsIdentity);

        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.set(key, objectMapper.writeValueAsString(stateId));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getStateWaitingForSms(String smsIdentity) {
        String key = String.format(REDIS_KEY_FLOW_WAITING_SMS_REPLY, smsIdentity);

        try (Jedis jedis = jedisPool.getResource()) {
            String state = jedis.get(key);

            if (StringUtils.isNotEmpty(state)) {
                return objectMapper.readValue(state, String.class);
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void deleteStateWaitingForSms(String smsIdentity) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(String.format(REDIS_KEY_FLOW_WAITING_SMS_REPLY, smsIdentity));
        }
    }

    public void saveSimpleCall(String callSid) {
        String key = String.format(REDIS_KEY_SIMPLE_CALL, callSid);

        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.set(key, objectMapper.writeValueAsString(callSid));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isSimpleCall(String callSid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(String.format(REDIS_KEY_SIMPLE_CALL, callSid));
        }
    }
}
