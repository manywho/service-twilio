package com.manywho.services.test;

import com.manywho.services.test.mocks.HttpClientMock;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FlowClientTester extends HttpClientMock {
    protected ArrayList<HttpResponse> responses;
    protected ArrayList <HttpResponse> responsesHistory;
    protected ArrayList <HttpUriRequest> requestsHistory;

    public FlowClientTester() {
        responses = new ArrayList<>();
        responsesHistory = new ArrayList<>();
        requestsHistory = new ArrayList<>();
    }

    public void addResponse(HttpResponse response) {
        responses.add(response);
    }

    public ArrayList<HttpResponse> getResponsesHistory() {
        return responsesHistory;
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        responsesHistory.add(responses.get(0));
        responses.remove(0);

        requestsHistory.add(request);

        return responsesHistory.get(responsesHistory.size()-1);
    }

    public String getExpectedRequestBody(Integer index) throws IOException {
        HttpUriRequest request = requestsHistory.get(index);
        return IOUtils.toString(((HttpPost) request).getEntity().getContent(), (Charset) ((HttpPost) request).getEntity().getContentEncoding());
    }

    public Header getExpectedRequestHeader(Integer index, String headerName) {
        return Arrays.asList(requestsHistory.get(index).getAllHeaders()).stream()
                .filter(h -> Objects.equals(h.getName(), headerName))
                .findFirst().get();
    }
}
