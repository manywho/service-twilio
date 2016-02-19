package com.manywho.services.test;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;

public class HttpClientForTest extends CloseableHttpClient{

    private ArrayList <CloseableHttpResponse> responses;
    private ArrayList <CloseableHttpResponse> responsesHistory;

    HttpClientForTest() {
        responses = new ArrayList<>();
        responsesHistory = new ArrayList<>();
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

        return response;
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
