package com.manywho.services.test.mocks;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.util.Locale;

public class HttpResponseMock implements CloseableHttpResponse{

    protected StatusLine statusLine;
    protected Header[] headers;
    protected HttpEntity httpEntity;

    public HttpResponseMock() {
        this.statusLine = null;
        this.headers = null;
        this.httpEntity = null;
    }

    public HttpResponseMock(StatusLine statusLine, Header[] headers, HttpEntity httpEntity) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.httpEntity = httpEntity;
    }

    @Override
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override
    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    @Override
    public void setStatusLine(ProtocolVersion protocolVersion, int i) {}

    @Override
    public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {}

    @Override
    public void setStatusCode(int i) throws IllegalStateException {}

    @Override
    public void setReasonPhrase(String s) throws IllegalStateException {}

    @Override
    public HttpEntity getEntity() {
        return httpEntity;
    }

    @Override
    public void setEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale locale) {}

    @Override
    public ProtocolVersion getProtocolVersion() {
        return null;
    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public Header[] getHeaders(String s) {
        return headers;
    }

    @Override
    public Header getFirstHeader(String s) {
        return null;
    }

    @Override
    public Header getLastHeader(String s) {
        return null;
    }

    @Override
    public Header[] getAllHeaders() {
        return headers;
    }

    @Override
    public void addHeader(Header header) {}

    @Override
    public void addHeader(String s, String s1) {}

    @Override
    public void setHeader(Header header) {}

    @Override
    public void setHeader(String s, String s1) {}

    @Override
    public void setHeaders(Header[] headers) {}

    @Override
    public void removeHeader(Header header) {
        this.headers = ArrayUtils.removeElement(this.headers, header.getName());
    }

    @Override
    public void removeHeaders(String s) {
        this.headers = ArrayUtils.removeElement(this.headers, s);
    }

    @Override
    public HeaderIterator headerIterator() {
        return null;
    }

    @Override
    public HeaderIterator headerIterator(String s) {
        return null;
    }

    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public void setParams(HttpParams httpParams) {}

    protected StatusLine getNewStatusLine(String protocol, int mayorVersion, int minorVersion, int statusCode, String reasonPhrase) {
        return new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return new ProtocolVersion(protocol, mayorVersion, minorVersion);
            }

            @Override
            public int getStatusCode() {
                return statusCode;
            }

            @Override
            public String getReasonPhrase() {
                return reasonPhrase;
            }
        };
    }

    protected HttpEntity getNewEntity(String contentType, String content) {
        return new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return 0;
            }

            @Override
            public Header getContentType() {
                return new BasicHeader("Content-Type", contentType);
            }

            @Override
            public Header getContentEncoding() {
                return null;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                return new ByteArrayInputStream(content.getBytes());
            }

            @Override
            public void writeTo(OutputStream outstream) throws IOException {

            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {

            }
        };
    }

    @Override
    public void close() throws IOException {

    }
}