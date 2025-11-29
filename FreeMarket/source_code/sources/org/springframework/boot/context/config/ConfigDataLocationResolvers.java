package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.util.Instantiator;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLocationResolvers.class */
class ConfigDataLocationResolvers {
    private final List<ConfigDataLocationResolver<?>> resolvers;

    ConfigDataLocationResolvers(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader) {
        this(logFactory, bootstrapContext, binder, resourceLoader, SpringFactoriesLoader.loadFactoryNames(ConfigDataLocationResolver.class, resourceLoader.getClassLoader()));
    }

    ConfigDataLocationResolvers(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader, List<String> names) {
        Instantiator<ConfigDataLocationResolver<?>> instantiator = new Instantiator<>(ConfigDataLocationResolver.class, availableParameters -> {
            logFactory.getClass();
            availableParameters.add(Log.class, logFactory::getLog);
            availableParameters.add(DeferredLogFactory.class, logFactory);
            availableParameters.add(Binder.class, binder);
            availableParameters.add(ResourceLoader.class, resourceLoader);
            availableParameters.add(ConfigurableBootstrapContext.class, bootstrapContext);
            availableParameters.add(BootstrapContext.class, bootstrapContext);
            availableParameters.add(BootstrapRegistry.class, bootstrapContext);
        });
        this.resolvers = reorder(instantiator.instantiate(resourceLoader.getClassLoader(), names));
    }

    private List<ConfigDataLocationResolver<?>> reorder(List<ConfigDataLocationResolver<?>> resolvers) {
        List<ConfigDataLocationResolver<?>> reordered = new ArrayList<>(resolvers.size());
        StandardConfigDataLocationResolver resourceResolver = null;
        for (ConfigDataLocationResolver<?> resolver : resolvers) {
            if (resolver instanceof StandardConfigDataLocationResolver) {
                resourceResolver = (StandardConfigDataLocationResolver) resolver;
            } else {
                reordered.add(resolver);
            }
        }
        if (resourceResolver != null) {
            reordered.add(resourceResolver);
        }
        return Collections.unmodifiableList(reordered);
    }

    List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) {
        if (location == null) {
            return Collections.emptyList();
        }
        for (ConfigDataLocationResolver<?> resolver : getResolvers()) {
            if (resolver.isResolvable(context, location)) {
                return resolve(resolver, context, location, profiles);
            }
        }
        throw new UnsupportedConfigDataLocationException(location);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolver<?> resolver, ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) {
        List<ConfigDataResolutionResult> resolved = resolve(location, false, () -> {
            return resolver.resolve(context, location);
        });
        if (profiles == null) {
            return resolved;
        }
        List<ConfigDataResolutionResult> profileSpecific = resolve(location, true, () -> {
            return resolver.resolveProfileSpecific(context, location, profiles);
        });
        return merge(resolved, profileSpecific);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocation location, boolean profileSpecific, Supplier<List<? extends ConfigDataResource>> resolveAction) {
        List<ConfigDataResource> resources = nonNullList(resolveAction.get());
        List<ConfigDataResolutionResult> resolved = new ArrayList<>(resources.size());
        for (ConfigDataResource resource : resources) {
            resolved.add(new ConfigDataResolutionResult(location, resource, profileSpecific));
        }
        return resolved;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> List<T> nonNullList(List<? extends T> list) {
        return list != 0 ? list : Collections.emptyList();
    }

    private <T> List<T> merge(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1.size() + list2.size());
        merged.addAll(list1);
        merged.addAll(list2);
        return merged;
    }

    List<ConfigDataLocationResolver<?>> getResolvers() {
        return this.resolvers;
    }
}
