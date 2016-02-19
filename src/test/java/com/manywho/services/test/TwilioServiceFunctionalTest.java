package com.manywho.services.test;

import com.fiftyonred.mock_jedis.MockJedis;
import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.common.io.Files;
import com.manywho.sdk.client.RunClient;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.services.providers.ObjectMapperProvider;
import com.manywho.sdk.test.FunctionalTest;
import com.manywho.sdk.test.MockFactory;
import com.manywho.services.twilio.factories.TwilioRestClientFactory;
import com.manywho.services.twilio.managers.CacheManager;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import junit.framework.TestCase;
import org.apache.commons.io.Charsets;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwilioServiceFunctionalTest extends FunctionalTest
{
    protected MockJedis mockJedis;
    protected TwilioRestClient mockTwilioRestClient;
    protected SmsFactory mockSmsFactory;
    protected Account mockAccount;
    protected TwilioRestClientFactory mockTwilioClientFactory;
    protected MessageFactory mockMessageFactory;
    protected CallFactory mockCallFactory;
    protected HttpClientForTest mockHttpClient;

    private RunClient runClient;

    @Override
    protected Application configure(){

        MockJedisPool mockJedisPool = new MockJedisPool(new GenericObjectPoolConfig(), "localhost");
        mockJedis = (MockJedis) mockJedisPool.getResource();
        mockAccount = mock(Account.class);
        mockSmsFactory = mock(SmsFactory.class);
        mockMessageFactory = mock(MessageFactory.class);
        mockTwilioRestClient = mock(TwilioRestClient.class);
        mockTwilioClientFactory = mock(TwilioRestClientFactory.class);
        mockCallFactory = mock(CallFactory.class);
        mockHttpClient = new HttpClientForTest();

        runClient = new RunClient(mockHttpClient);

        when(mockAccount.getSmsFactory()).thenReturn(mockSmsFactory);
        when(mockAccount.getMessageFactory()).thenReturn(mockMessageFactory);
        when(mockAccount.getCallFactory()).thenReturn(mockCallFactory);
        when(mockTwilioRestClient.getAccount()).thenReturn(mockAccount);


        return new com.manywho.services.twilio.Application().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new MockFactory<MockJedisPool>(mockJedisPool)).to(JedisPool.class).ranked(1);
                bindFactory(new MockFactory<TwilioRestClientFactory>(mockTwilioClientFactory)).to(TwilioRestClientFactory.class).ranked(1);
                bind(CacheManager.class).to(CacheManager.class);
                bindFactory(new MockFactory<RunClient>(runClient)).to(RunClient.class).ranked(1);
            }
        });
    }

    public String getFileContent(String filePath) throws URISyntaxException, IOException {
        return Files.toString(getFile(filePath),  Charsets.UTF_8);
    }

    public void assertXMLEqual(String description, String xml1, String xml2) throws IOException, SAXException, ParserConfigurationException {
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(getXmlDocumentFromString(xml1), getXmlDocumentFromString(xml2));
        TestCase.assertTrue("XML similar "+ "(" + description + ")" + myDiff.toString(), myDiff.similar());
    }

    private Document getXmlDocumentFromString(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse( new InputSource( new StringReader( xmlString ) ) );
    }

    protected Entity<ObjectDataRequest> getObjectDataRequestFromFile(String filePath) throws URISyntaxException, IOException {
        ObjectDataRequest objectRequest = (ObjectDataRequest) ObjectMapperProvider.getObjectMapper().readValue(this.getFile(filePath), ObjectDataRequest.class);
        return Entity.entity(objectRequest, MediaType.APPLICATION_JSON_TYPE);
    }
}
