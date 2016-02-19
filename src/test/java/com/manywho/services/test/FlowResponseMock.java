package com.manywho.services.test;

import com.manywho.services.test.mocks.HttpResponseMock;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class FlowResponseMock extends HttpResponseMock {
    public FlowResponseMock() {
        this.headers = getFullListHeaders();
        this.httpEntity = getNewEntity("Content-Type: application/json; charset=utf-8", "\"WAIT\"");
        this.statusLine = getNewStatusLine("HTTP", 1, 1, 200, "ok");
    }

    public FlowResponseMock(Header[] headers, String protocol, int mayorVersion, int minorVersion,
                            int statusCode, String reasonPhrase, String contentType, String body ) {

        this.headers = headers;
        this.statusLine = getNewStatusLine(protocol, mayorVersion, minorVersion, statusCode, reasonPhrase);
        this.httpEntity = getNewEntity(contentType, body);
    }

    public static Header[] getFullListHeaders() {
        return new Header[] {
                new BasicHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers: Authorization, ManyWhoTenant, Culture, ManyWhoState, Origin, Cache-Control, X-Requested-With, Content-Type, Accept"),
                new BasicHeader("Access-Control-Allow-Methods", "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS" ),
                new BasicHeader("Access-Control-Allow-Origin", "Access-Control-Allow-Origin: *"),
                new BasicHeader("Allow", "Allow: DELETE"),
                new BasicHeader("Cache-Control", "Cache-Control: private"),
                new BasicHeader("Content-Type", "Content-Type: application/json; charset=utf-8"),
                new BasicHeader("Date", "Date: Fri, 19 Feb 2016 10:08:12 GMT"),
                new BasicHeader("Server", "Server: Microsoft-IIS/8.5"),
                new BasicHeader("X-AspNet-Version", "X-AspNet-Version: 4.0.30319"),
                new BasicHeader("X-Powered-By", "X-Powered-By: ASP.NET"),
                new BasicHeader("Content-Length", "Content-Length: 6"),
                new BasicHeader("Connection", "Connection: keep-alive")
        };
    }


}
