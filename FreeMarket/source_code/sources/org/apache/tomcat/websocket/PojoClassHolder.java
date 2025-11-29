package org.apache.tomcat.websocket;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/PojoClassHolder.class */
public class PojoClassHolder implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoClassHolder.class);
    private final Class<?> pojoClazz;
    private final ClientEndpointConfig clientEndpointConfig;

    public PojoClassHolder(Class<?> pojoClazz, ClientEndpointConfig clientEndpointConfig) {
        this.pojoClazz = pojoClazz;
        this.clientEndpointConfig = clientEndpointConfig;
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public String getClassName() {
        return this.pojoClazz.getName();
    }

    @Override // org.apache.tomcat.websocket.ClientEndpointHolder
    public Endpoint getInstance(InstanceManager instanceManager) throws IllegalAccessException, InstantiationException, NamingException, IllegalArgumentException, DeploymentException, InvocationTargetException {
        Object pojo;
        try {
            if (instanceManager == null) {
                pojo = this.pojoClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                pojo = instanceManager.newInstance(this.pojoClazz);
            }
            return new PojoEndpointClient(pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
        } catch (ReflectiveOperationException | SecurityException | NamingException e) {
            throw new DeploymentException(sm.getString("clientEndpointHolder.instanceCreationFailed"), e);
        }
    }
}
