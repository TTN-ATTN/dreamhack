package org.apache.tomcat.websocket;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/ClientEndpointHolder.class */
public interface ClientEndpointHolder {
    String getClassName();

    Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException;
}
