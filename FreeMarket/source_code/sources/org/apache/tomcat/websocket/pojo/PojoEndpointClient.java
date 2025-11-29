package org.apache.tomcat.websocket.pojo;

import java.util.Collections;
import java.util.List;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.InstanceManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/pojo/PojoEndpointClient.class */
public class PojoEndpointClient extends PojoEndpointBase {
    @Deprecated
    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders) throws DeploymentException {
        super(Collections.emptyMap());
        setPojo(pojo);
        setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null));
    }

    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders, InstanceManager instanceManager) throws DeploymentException {
        super(Collections.emptyMap());
        setPojo(pojo);
        setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null, instanceManager));
    }

    @Override // javax.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig config) {
        doOnOpen(session, config);
    }
}
