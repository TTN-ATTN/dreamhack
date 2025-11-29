package org.springframework.boot.web.context;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.server.WebServerFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/context/MissingWebServerFactoryBeanException.class */
public class MissingWebServerFactoryBeanException extends NoSuchBeanDefinitionException {
    private final WebApplicationType webApplicationType;

    public MissingWebServerFactoryBeanException(Class<? extends WebServerApplicationContext> webServerApplicationContextClass, Class<? extends WebServerFactory> webServerFactoryClass, WebApplicationType webApplicationType) {
        super(webServerFactoryClass, String.format("Unable to start %s due to missing %s bean", webServerApplicationContextClass.getSimpleName(), webServerFactoryClass.getSimpleName()));
        this.webApplicationType = webApplicationType;
    }

    public WebApplicationType getWebApplicationType() {
        return this.webApplicationType;
    }
}
