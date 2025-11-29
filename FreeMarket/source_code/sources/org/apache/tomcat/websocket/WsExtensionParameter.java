package org.apache.tomcat.websocket;

import javax.websocket.Extension;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/WsExtensionParameter.class */
public class WsExtensionParameter implements Extension.Parameter {
    private final String name;
    private final String value;

    WsExtensionParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override // javax.websocket.Extension.Parameter
    public String getName() {
        return this.name;
    }

    @Override // javax.websocket.Extension.Parameter
    public String getValue() {
        return this.value;
    }
}
