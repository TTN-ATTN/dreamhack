package org.springframework.boot.web.servlet.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.ServletContextAwareProcessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/context/WebApplicationContextServletContextAwareProcessor.class */
public class WebApplicationContextServletContextAwareProcessor extends ServletContextAwareProcessor {
    private final ConfigurableWebApplicationContext webApplicationContext;

    public WebApplicationContextServletContextAwareProcessor(ConfigurableWebApplicationContext webApplicationContext) {
        Assert.notNull(webApplicationContext, "WebApplicationContext must not be null");
        this.webApplicationContext = webApplicationContext;
    }

    @Override // org.springframework.web.context.support.ServletContextAwareProcessor
    protected ServletContext getServletContext() {
        ServletContext servletContext = this.webApplicationContext.getServletContext();
        return servletContext != null ? servletContext : super.getServletContext();
    }

    @Override // org.springframework.web.context.support.ServletContextAwareProcessor
    protected ServletConfig getServletConfig() {
        ServletConfig servletConfig = this.webApplicationContext.getServletConfig();
        return servletConfig != null ? servletConfig : super.getServletConfig();
    }
}
