package org.apache.coyote.http2;

import org.apache.coyote.http2.HpackDecoder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/HeaderSink.class */
class HeaderSink implements HpackDecoder.HeaderEmitter {
    HeaderSink() {
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void emitHeader(String name, String value) {
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void validateHeaders() throws StreamException {
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void setHeaderException(StreamException streamException) {
    }
}
