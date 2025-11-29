package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Future;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsRemoteEndpointAsync.class */
public class WsRemoteEndpointAsync extends WsRemoteEndpointBase implements RemoteEndpoint.Async {
    WsRemoteEndpointAsync(WsRemoteEndpointImplBase base) {
        super(base);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public long getSendTimeout() {
        return this.base.getSendTimeout();
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void setSendTimeout(long timeout) {
        this.base.setSendTimeout(timeout);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendText(String text, SendHandler completion) throws NoSuchAlgorithmException {
        this.base.sendStringByCompletion(text, completion);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendText(String text) {
        return this.base.sendStringByFuture(text);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendBinary(ByteBuffer data) {
        return this.base.sendBytesByFuture(data);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendBinary(ByteBuffer data, SendHandler completion) throws NoSuchAlgorithmException {
        this.base.sendBytesByCompletion(data, completion);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public Future<Void> sendObject(Object obj) {
        return this.base.sendObjectByFuture(obj);
    }

    @Override // javax.websocket.RemoteEndpoint.Async
    public void sendObject(Object obj, SendHandler completion) throws NoSuchAlgorithmException, IOException, EncodeException {
        this.base.sendObjectByCompletion(obj, completion);
    }
}
