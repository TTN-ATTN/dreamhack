package javax.websocket.server;

import java.util.Set;
import javax.websocket.Endpoint;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/server/ServerApplicationConfig.class */
public interface ServerApplicationConfig {
    Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set);

    Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set);
}
