package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import javax.websocket.PongMessage;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsPongMessage.class */
public class WsPongMessage implements PongMessage {
    private final ByteBuffer applicationData;

    public WsPongMessage(ByteBuffer applicationData) {
        byte[] dst = new byte[applicationData.limit()];
        applicationData.get(dst);
        this.applicationData = ByteBuffer.wrap(dst);
    }

    @Override // javax.websocket.PongMessage
    public ByteBuffer getApplicationData() {
        return this.applicationData;
    }
}
