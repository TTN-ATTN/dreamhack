package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/EndpointClassHolder.class */
public class EndpointClassHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) EndpointClassHolder.class);
    private final Class<? extends Endpoint> clazz;

    public EndpointClassHolder(Class<? extends Endpoint> clazz) {
        this.clazz = clazz;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.clazz.getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        try {
            if (instanceManager == null) {
                return this.clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            return (Endpoint) instanceManager.newInstance((Class<?>) this.clazz);
        } catch (ReflectiveOperationException | NamingException e) {
            throw new DeploymentException(sm.getString("clientEndpointHolder.instanceCreationFailed"), e);
        }
    }
}
