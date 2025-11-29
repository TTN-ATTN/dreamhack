package org.springframework.web.context;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/ContextCleanupListener.class */
public class ContextCleanupListener implements ServletContextListener {
    private static final Log logger = LogFactory.getLog((Class<?>) ContextCleanupListener.class);

    @Override // javax.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
    }

    @Override // javax.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        cleanupAttributes(event.getServletContext());
    }

    static void cleanupAttributes(ServletContext servletContext) {
        Enumeration<String> attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                Object attrValue = servletContext.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    try {
                        ((DisposableBean) attrValue).destroy();
                    } catch (Throwable ex) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Invocation of destroy method failed on ServletContext attribute with name '" + attrName + "'", ex);
                        }
                    }
                }
            }
        }
    }
}
