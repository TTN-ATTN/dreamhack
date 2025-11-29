package org.apache.coyote.http2;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/StreamException.class */
class StreamException extends Http2Exception {
    private static final long serialVersionUID = 1;
    private final int streamId;

    StreamException(String msg, Http2Error error, int streamId) {
        super(msg, error);
        this.streamId = streamId;
    }

    int getStreamId() {
        return this.streamId;
    }
}
