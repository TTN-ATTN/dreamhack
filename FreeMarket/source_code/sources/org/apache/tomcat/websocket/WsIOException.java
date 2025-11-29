package org.apache.tomcat.websocket;

import java.io.IOException;
import javax.websocket.CloseReason;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsIOException.class */
public class WsIOException extends IOException {
    private static final long serialVersionUID = 1;
    private final CloseReason closeReason;

    public WsIOException(CloseReason closeReason) {
        this.closeReason = closeReason;
    }

    public CloseReason getCloseReason() {
        return this.closeReason;
    }
}
