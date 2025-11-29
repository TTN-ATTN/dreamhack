package org.springframework.boot.rsocket.context;

import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.context.ApplicationEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/rsocket/context/RSocketServerInitializedEvent.class */
public class RSocketServerInitializedEvent extends ApplicationEvent {
    public RSocketServerInitializedEvent(RSocketServer server) {
        super(server);
    }

    public RSocketServer getServer() {
        return getSource();
    }

    @Override // java.util.EventObject
    public RSocketServer getSource() {
        return (RSocketServer) super.getSource();
    }
}
