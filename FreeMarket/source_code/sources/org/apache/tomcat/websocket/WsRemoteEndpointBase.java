package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import javax.websocket.RemoteEndpoint;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsRemoteEndpointBase.class */
public abstract class WsRemoteEndpointBase implements RemoteEndpoint {
    protected final WsRemoteEndpointImplBase base;

    WsRemoteEndpointBase(WsRemoteEndpointImplBase base) {
        this.base = base;
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void setBatchingAllowed(boolean batchingAllowed) throws NoSuchAlgorithmException, IOException {
        this.base.setBatchingAllowed(batchingAllowed);
    }

    @Override // javax.websocket.RemoteEndpoint
    public final boolean getBatchingAllowed() {
        return this.base.getBatchingAllowed();
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void flushBatch() throws NoSuchAlgorithmException, IOException {
        this.base.flushBatch();
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void sendPing(ByteBuffer applicationData) throws NoSuchAlgorithmException, IOException, IllegalArgumentException {
        this.base.sendPing(applicationData);
    }

    @Override // javax.websocket.RemoteEndpoint
    public final void sendPong(ByteBuffer applicationData) throws NoSuchAlgorithmException, IOException, IllegalArgumentException {
        this.base.sendPong(applicationData);
    }
}
