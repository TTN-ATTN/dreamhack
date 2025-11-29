package javax.servlet.http;

import java.util.EventListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/HttpSessionAttributeListener.class */
public interface HttpSessionAttributeListener extends EventListener {
    default void attributeAdded(HttpSessionBindingEvent se) {
    }

    default void attributeRemoved(HttpSessionBindingEvent se) {
    }

    default void attributeReplaced(HttpSessionBindingEvent se) {
    }
}
