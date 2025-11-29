package org.apache.tomcat.websocket.pojo;

import java.util.Map;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/pojo/PojoEndpointServer.class */
public class PojoEndpointServer extends PojoEndpointBase {
    public PojoEndpointServer(Map<String, String> pathParameters, Object pojo) {
        super(pathParameters);
        setPojo(pojo);
    }

    @Override // javax.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ServerEndpointConfig sec = (ServerEndpointConfig) endpointConfig;
        PojoMethodMapping methodMapping = (PojoMethodMapping) sec.getUserProperties().get(Constants.POJO_METHOD_MAPPING_KEY);
        setMethodMapping(methodMapping);
        doOnOpen(session, endpointConfig);
    }
}
