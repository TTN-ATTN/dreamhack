package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.Method;
import javax.websocket.Session;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerPartialText.class */
public class PojoMessageHandlerPartialText extends PojoMessageHandlerPartialBase<String> {
    public PojoMessageHandlerPartialText(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexBoolean, indexSession, maxMessageSize);
    }
}
