package javax.websocket.server;

import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/server/ServerContainer.class */
public interface ServerContainer extends WebSocketContainer {
    void addEndpoint(Class<?> cls) throws DeploymentException;

    void addEndpoint(ServerEndpointConfig serverEndpointConfig) throws DeploymentException;
}
