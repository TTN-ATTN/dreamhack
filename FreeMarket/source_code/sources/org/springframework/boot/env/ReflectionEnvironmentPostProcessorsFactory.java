package org.springframework.boot.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.util.Instantiator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/ReflectionEnvironmentPostProcessorsFactory.class */
class ReflectionEnvironmentPostProcessorsFactory implements EnvironmentPostProcessorsFactory {
    private final List<Class<?>> classes;
    private ClassLoader classLoader;
    private final List<String> classNames;

    ReflectionEnvironmentPostProcessorsFactory(Class<?>... classes) {
        this.classes = new ArrayList(Arrays.asList(classes));
        this.classNames = null;
    }

    ReflectionEnvironmentPostProcessorsFactory(ClassLoader classLoader, String... classNames) {
        this(classLoader, (List<String>) Arrays.asList(classNames));
    }

    ReflectionEnvironmentPostProcessorsFactory(ClassLoader classLoader, List<String> classNames) {
        this.classes = null;
        this.classLoader = classLoader;
        this.classNames = classNames;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessorsFactory
    public List<EnvironmentPostProcessor> getEnvironmentPostProcessors(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        Instantiator<EnvironmentPostProcessor> instantiator = new Instantiator<>(EnvironmentPostProcessor.class, parameters -> {
            parameters.add(DeferredLogFactory.class, logFactory);
            logFactory.getClass();
            parameters.add(Log.class, logFactory::getLog);
            parameters.add(ConfigurableBootstrapContext.class, bootstrapContext);
            parameters.add(BootstrapContext.class, bootstrapContext);
            parameters.add(BootstrapRegistry.class, bootstrapContext);
        });
        return this.classes != null ? instantiator.instantiateTypes(this.classes) : instantiator.instantiate(this.classLoader, this.classNames);
    }
}
