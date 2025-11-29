package org.springframework.boot;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/DefaultApplicationContextFactory.class */
class DefaultApplicationContextFactory implements ApplicationContextFactory {
    DefaultApplicationContextFactory() {
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
        return (Class) getFromSpringFactories(webApplicationType, (v0, v1) -> {
            return v0.getEnvironmentType(v1);
        }, null);
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return (ConfigurableEnvironment) getFromSpringFactories(webApplicationType, (v0, v1) -> {
            return v0.createEnvironment(v1);
        }, null);
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
        try {
            return (ConfigurableApplicationContext) getFromSpringFactories(webApplicationType, (v0, v1) -> {
                return v0.create(v1);
            }, AnnotationConfigApplicationContext::new);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable create a default ApplicationContext instance, you may need a custom ApplicationContextFactory", ex);
        }
    }

    private <T> T getFromSpringFactories(WebApplicationType webApplicationType, BiFunction<ApplicationContextFactory, WebApplicationType, T> action, Supplier<T> defaultResult) {
        for (ApplicationContextFactory candidate : SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class, getClass().getClassLoader())) {
            T result = action.apply(candidate, webApplicationType);
            if (result != null) {
                return result;
            }
        }
        if (defaultResult != null) {
            return defaultResult.get();
        }
        return null;
    }
}
