package org.apache.coyote.http2;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/ConnectionException.class */
class ConnectionException extends Http2Exception {
    private static final long serialVersionUID = 1;

    ConnectionException(String msg, Http2Error error) {
        super(msg, error);
    }

    ConnectionException(String msg, Http2Error error, Throwable cause) {
        super(msg, error, cause);
    }
}
