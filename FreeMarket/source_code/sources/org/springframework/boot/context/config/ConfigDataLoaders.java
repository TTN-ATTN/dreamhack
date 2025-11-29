package org.springframework.boot.context.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.util.Instantiator;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLoaders.class */
class ConfigDataLoaders {
    private final Log logger;
    private final List<ConfigDataLoader<?>> loaders;
    private final List<Class<?>> resourceTypes;

    ConfigDataLoaders(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, ClassLoader classLoader) {
        this(logFactory, bootstrapContext, classLoader, SpringFactoriesLoader.loadFactoryNames(ConfigDataLoader.class, classLoader));
    }

    ConfigDataLoaders(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, ClassLoader classLoader, List<String> names) {
        this.logger = logFactory.getLog(getClass());
        Instantiator<ConfigDataLoader<?>> instantiator = new Instantiator<>(ConfigDataLoader.class, availableParameters -> {
            logFactory.getClass();
            availableParameters.add(Log.class, logFactory::getLog);
            availableParameters.add(DeferredLogFactory.class, logFactory);
            availableParameters.add(ConfigurableBootstrapContext.class, bootstrapContext);
            availableParameters.add(BootstrapContext.class, bootstrapContext);
            availableParameters.add(BootstrapRegistry.class, bootstrapContext);
        });
        this.loaders = instantiator.instantiate(classLoader, names);
        this.resourceTypes = getResourceTypes(this.loaders);
    }

    private List<Class<?>> getResourceTypes(List<ConfigDataLoader<?>> loaders) {
        List<Class<?>> resourceTypes = new ArrayList<>(loaders.size());
        for (ConfigDataLoader<?> loader : loaders) {
            resourceTypes.add(getResourceType(loader));
        }
        return Collections.unmodifiableList(resourceTypes);
    }

    private Class<?> getResourceType(ConfigDataLoader<?> loader) {
        return ResolvableType.forClass(loader.getClass()).as(ConfigDataLoader.class).resolveGeneric(new int[0]);
    }

    <R extends ConfigDataResource> ConfigData load(ConfigDataLoaderContext context, R resource) throws IOException {
        ConfigDataLoader<R> loader = getLoader(context, resource);
        this.logger.trace(LogMessage.of(() -> {
            return "Loading " + resource + " using loader " + loader.getClass().getName();
        }));
        return loader.load(context, resource);
    }

    private <R extends ConfigDataResource> ConfigDataLoader<R> getLoader(ConfigDataLoaderContext context, R resource) {
        ConfigDataLoader<R> result = null;
        for (int i = 0; i < this.loaders.size(); i++) {
            ConfigDataLoader<R> configDataLoader = (ConfigDataLoader) this.loaders.get(i);
            if (this.resourceTypes.get(i).isInstance(resource) && configDataLoader.isLoadable(context, resource)) {
                if (result != null) {
                    throw new IllegalStateException("Multiple loaders found for resource '" + resource + "' [" + configDataLoader.getClass().getName() + "," + result.getClass().getName() + "]");
                }
                result = configDataLoader;
            }
        }
        Assert.state(result != null, (Supplier<String>) () -> {
            return "No loader found for resource '" + resource + "'";
        });
        return result;
    }
}
