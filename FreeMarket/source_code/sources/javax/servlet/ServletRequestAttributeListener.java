package javax.servlet;

import java.util.EventListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/ServletRequestAttributeListener.class */
public interface ServletRequestAttributeListener extends EventListener {
    default void attributeAdded(ServletRequestAttributeEvent srae) {
    }

    default void attributeRemoved(ServletRequestAttributeEvent srae) {
    }

    default void attributeReplaced(ServletRequestAttributeEvent srae) {
    }
}
