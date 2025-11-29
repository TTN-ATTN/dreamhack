package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/PojoHolder.class */
public class PojoHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoHolder.class);
    private final Object pojo;
    private final ClientEndpointConfig clientEndpointConfig;

    public PojoHolder(Object pojo, ClientEndpointConfig clientEndpointConfig) {
        this.pojo = pojo;
        this.clientEndpointConfig = clientEndpointConfig;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.pojo.getClass().getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws NamingException, DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(this.pojo);
            } catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("clientEndpointHolder.instanceRegistrationFailed"), e);
            }
        }
        return new PojoEndpointClient(this.pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
    }
}
