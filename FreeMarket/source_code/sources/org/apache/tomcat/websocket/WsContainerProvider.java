package org.apache.tomcat.websocket;

import aQute.bnd.annotation.spi.ServiceProvider;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

@ServiceProvider(ContainerProvider.class)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsContainerProvider.class */
public class WsContainerProvider extends ContainerProvider {
    @Override // javax.websocket.ContainerProvider
    protected WebSocketContainer getContainer() {
        return new WsWebSocketContainer();
    }
}
