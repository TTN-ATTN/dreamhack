package org.apache.tomcat.websocket.server;

import java.util.Map;
import javax.websocket.server.ServerEndpointConfig;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/server/WsMappingResult.class */
class WsMappingResult {
    private final ServerEndpointConfig config;
    private final Map<String, String> pathParams;

    WsMappingResult(ServerEndpointConfig config, Map<String, String> pathParams) {
        this.config = config;
        this.pathParams = pathParams;
    }

    ServerEndpointConfig getConfig() {
        return this.config;
    }

    Map<String, String> getPathParams() {
        return this.pathParams;
    }
}
