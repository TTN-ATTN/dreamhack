package org.apache.coyote.http2;

import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http2/RecycledStream.class */
class RecycledStream extends AbstractNonZeroStream {
    private final String connectionId;
    private int remainingFlowControlWindow;

    RecycledStream(String connectionId, Integer identifier, StreamStateMachine state, int remainingFlowControlWindow) {
        super(identifier, state);
        this.connectionId = connectionId;
        this.remainingFlowControlWindow = remainingFlowControlWindow;
    }

    @Override // org.apache.coyote.http2.AbstractStream
    String getConnectionId() {
        return this.connectionId;
    }

    @Override // org.apache.coyote.http2.AbstractStream
    void incrementWindowSize(int increment) throws Http2Exception {
    }

    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    void receivedData(int payloadSize) throws ConnectionException {
        this.remainingFlowControlWindow -= payloadSize;
    }

    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    ByteBuffer getInputByteBuffer() {
        if (this.remainingFlowControlWindow < 0) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return null;
    }
}
