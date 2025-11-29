package org.springframework.boot.web.reactive.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/AnnotationConfigReactiveWebApplicationContext.class */
public class AnnotationConfigReactiveWebApplicationContext extends AnnotationConfigApplicationContext implements ConfigurableReactiveWebApplicationContext {
    public AnnotationConfigReactiveWebApplicationContext() {
    }

    public AnnotationConfigReactiveWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public AnnotationConfigReactiveWebApplicationContext(Class<?>... annotatedClasses) {
        super(annotatedClasses);
    }

    public AnnotationConfigReactiveWebApplicationContext(String... basePackages) {
        super(basePackages);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardReactiveWebEnvironment();
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        return new FilteredReactiveWebContextResource(path);
    }
}
