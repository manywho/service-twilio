package com.manywho.services.test;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HttpClientForTest extends CloseableHttpClient{

    private ArrayList <CloseableHttpResponse> responses;
    private ArrayList <CloseableHttpResponse> responsesHistory;
    protected ArrayList <HttpRequest> requestsHistory;

    public HttpClientForTest() {
        responses = new ArrayList<>();
        responsesHistory = new ArrayList<>();
        requestsHistory = new ArrayList<>();
    }

    public void addResponse(CloseableHttpResponse response) {
        responses.add(response);
    }

    public ArrayList<CloseableHttpResponse> getResponsesHistory(){
        return responsesHistory;
    }


    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        CloseableHttpResponse response = responses.get(0);
        responses.remove(0);
        responsesHistory.add(response);
        requestsHistory.add(request);

        return response;
    }

    public String getExpectedRequestBody(Integer index) throws IOException {
        HttpRequest request = requestsHistory.get(index);
        return IOUtils.toString(((HttpPost) request).getEntity().getContent(), (Charset) ((HttpPost) request).getEntity().getContentEncoding());
    }

    public Header getExpectedRequestHeader(Integer index, String headerName) {
        return Arrays.asList(requestsHistory.get(index).getAllHeaders()).stream()
                .filter(h -> Objects.equals(h.getName(), headerName))
                .findFirst().get();
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }
}
