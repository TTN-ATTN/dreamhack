package org.springframework.boot.env;

import java.util.List;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/EnvironmentPostProcessorsFactory.class */
public interface EnvironmentPostProcessorsFactory {
    List<EnvironmentPostProcessor> getEnvironmentPostProcessors(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext);

    static EnvironmentPostProcessorsFactory fromSpringFactories(ClassLoader classLoader) {
        return new ReflectionEnvironmentPostProcessorsFactory(classLoader, SpringFactoriesLoader.loadFactoryNames(EnvironmentPostProcessor.class, classLoader));
    }

    static EnvironmentPostProcessorsFactory of(Class<?>... classes) {
        return new ReflectionEnvironmentPostProcessorsFactory(classes);
    }

    static EnvironmentPostProcessorsFactory of(String... classNames) {
        return of(null, classNames);
    }

    static EnvironmentPostProcessorsFactory of(ClassLoader classLoader, String... classNames) {
        return new ReflectionEnvironmentPostProcessorsFactory(classLoader, classNames);
    }
}
