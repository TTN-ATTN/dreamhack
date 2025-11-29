package org.springframework.web.server.adapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/adapter/AbstractReactiveWebInitializer.class */
public abstract class AbstractReactiveWebInitializer implements WebApplicationInitializer {
    public static final String DEFAULT_SERVLET_NAME = "http-handler-adapter";

    protected abstract Class<?>[] getConfigClasses();

    @Override // org.springframework.web.WebApplicationInitializer
    public void onStartup(ServletContext servletContext) throws IllegalStateException, ServletException, BeansException {
        String servletName = getServletName();
        Assert.hasLength(servletName, "getServletName() must not return null or empty");
        ApplicationContext applicationContext = createApplicationContext();
        Assert.notNull(applicationContext, "createApplicationContext() must not return null");
        refreshApplicationContext(applicationContext);
        registerCloseListener(servletContext, applicationContext);
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);
        ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, servlet);
        if (registration == null) {
            throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. Check if there is another servlet registered under the same name.");
        }
        registration.setLoadOnStartup(1);
        registration.addMapping(getServletMapping());
        registration.setAsyncSupported(true);
    }

    protected String getServletName() {
        return DEFAULT_SERVLET_NAME;
    }

    protected ApplicationContext createApplicationContext() throws BeanDefinitionStoreException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Class<?>[] configClasses = getConfigClasses();
        Assert.notEmpty(configClasses, "No Spring configuration provided through getConfigClasses()");
        context.register(configClasses);
        return context;
    }

    protected void refreshApplicationContext(ApplicationContext context) throws IllegalStateException, BeansException {
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) context;
            if (!cac.isActive()) {
                cac.refresh();
            }
        }
    }

    protected void registerCloseListener(ServletContext servletContext, ApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) applicationContext;
            ServletContextDestroyedListener listener = new ServletContextDestroyedListener(cac);
            servletContext.addListener((ServletContext) listener);
        }
    }

    protected String getServletMapping() {
        return "/";
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/server/adapter/AbstractReactiveWebInitializer$ServletContextDestroyedListener.class */
    private static class ServletContextDestroyedListener implements ServletContextListener {
        private final ConfigurableApplicationContext applicationContext;

        public ServletContextDestroyedListener(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override // javax.servlet.ServletContextListener
        public void contextInitialized(ServletContextEvent sce) {
        }

        @Override // javax.servlet.ServletContextListener
        public void contextDestroyed(ServletContextEvent sce) {
            this.applicationContext.close();
        }
    }
}
