package javax.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/WebSocketContainer.class */
public interface WebSocketContainer {
    long getDefaultAsyncSendTimeout();

    void setAsyncSendTimeout(long j);

    Session connectToServer(Object obj, URI uri) throws IOException, DeploymentException;

    Session connectToServer(Class<?> cls, URI uri) throws IOException, DeploymentException;

    Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfig, URI uri) throws IOException, DeploymentException;

    Session connectToServer(Class<? extends Endpoint> cls, ClientEndpointConfig clientEndpointConfig, URI uri) throws IOException, DeploymentException;

    long getDefaultMaxSessionIdleTimeout();

    void setDefaultMaxSessionIdleTimeout(long j);

    int getDefaultMaxBinaryMessageBufferSize();

    void setDefaultMaxBinaryMessageBufferSize(int i);

    int getDefaultMaxTextMessageBufferSize();

    void setDefaultMaxTextMessageBufferSize(int i);

    Set<Extension> getInstalledExtensions();
}
