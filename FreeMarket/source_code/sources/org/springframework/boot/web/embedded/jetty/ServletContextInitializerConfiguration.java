package org.springframework.boot.web.embedded.jetty;

import javax.servlet.ServletException;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/ServletContextInitializerConfiguration.class */
public class ServletContextInitializerConfiguration extends AbstractConfiguration {
    private final ServletContextInitializer[] initializers;

    public ServletContextInitializerConfiguration(ServletContextInitializer... initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers = initializers;
    }

    public void configure(WebAppContext context) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            callInitializers(context);
            Thread.currentThread().setContextClassLoader(classLoader);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(classLoader);
            throw th;
        }
    }

    private void callInitializers(WebAppContext context) throws ServletException {
        try {
            setExtendedListenerTypes(context, true);
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(context.getServletContext());
            }
        } finally {
            setExtendedListenerTypes(context, false);
        }
    }

    private void setExtendedListenerTypes(WebAppContext context, boolean extended) {
        try {
            context.getServletContext().setExtendedListenerTypes(extended);
        } catch (NoSuchMethodError e) {
        }
    }
}
