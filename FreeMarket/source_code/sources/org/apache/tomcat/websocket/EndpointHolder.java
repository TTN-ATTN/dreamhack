package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/EndpointHolder.class */
public class EndpointHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) EndpointHolder.class);
    private final Endpoint endpoint;

    public EndpointHolder(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.endpoint.getClass().getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws NamingException, DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(this.endpoint);
            } catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("clientEndpointHolder.instanceRegistrationFailed"), e);
            }
        }
        return this.endpoint;
    }
}
