package org.apache.tomcat.websocket;

import javax.websocket.MessageHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/MessageHandlerResult.class */
public class MessageHandlerResult {
    private final MessageHandler handler;
    private final MessageHandlerResultType type;

    public MessageHandlerResult(MessageHandler handler, MessageHandlerResultType type) {
        this.handler = handler;
        this.type = type;
    }

    public MessageHandler getHandler() {
        return this.handler;
    }

    public MessageHandlerResultType getType() {
        return this.type;
    }
}
