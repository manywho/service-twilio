package com.manywho.services.test;

import com.fiftyonred.mock_jedis.MockJedis;
import com.fiftyonred.mock_jedis.MockJedisPool;
import com.manywho.sdk.test.FunctionalTest;
import com.manywho.sdk.test.MockFactory;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.configuration.TwilioConfiguration;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import redis.clients.jedis.JedisPool;
import javax.ws.rs.core.Application;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.inject.Singleton;

public class TwilioServiceFunctionalTest extends FunctionalTest
{
    protected MockJedis mockJedis;
    protected TwilioRestClient mockTwilioRestClient;
    protected SmsFactory mockSmsFactory;
    protected Account mockAccount;
    protected TwilioConfiguration mockTwilioConfiguration;
    protected TwilioRestClientFactory mockTwilioClientFactory;
    protected MessageFactory mockMessageFactory;

    @Override
    protected Application configure(){

        MockJedisPool mockJedisPool = new MockJedisPool(new GenericObjectPoolConfig(), "localhost");
        mockJedis = (MockJedis) mockJedisPool.getResource();
        mockAccount = mock(Account.class);
        mockSmsFactory = mock(SmsFactory.class);
        mockMessageFactory = mock(MessageFactory.class);
        mockTwilioRestClient = mock(TwilioRestClient.class);
        mockTwilioClientFactory = mock(TwilioRestClientFactory.class);
        mockTwilioConfiguration = mock(TwilioConfiguration.class);

        when(mockAccount.getSmsFactory()).thenReturn(mockSmsFactory);
        when(mockAccount.getMessageFactory()).thenReturn(mockMessageFactory);
        when(mockTwilioRestClient.getAccount()).thenReturn(mockAccount);

        return new com.manywho.services.twilio.Application().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new MockFactory<TwilioConfiguration>(mockTwilioConfiguration)).to(TwilioConfiguration.class).in(Singleton.class).ranked(1);
                bindFactory(new MockFactory<MockJedisPool>(mockJedisPool)).to(JedisPool.class).ranked(1);
                bindFactory(new MockFactory<TwilioRestClientFactory>(mockTwilioClientFactory)).to(TwilioRestClientFactory.class).ranked(1);
            }
        });
    }
}
