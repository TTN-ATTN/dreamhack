package javax.servlet;

import java.util.EventListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/ServletContextAttributeListener.class */
public interface ServletContextAttributeListener extends EventListener {
    default void attributeAdded(ServletContextAttributeEvent scae) {
    }

    default void attributeRemoved(ServletContextAttributeEvent scae) {
    }

    default void attributeReplaced(ServletContextAttributeEvent scae) {
    }
}
