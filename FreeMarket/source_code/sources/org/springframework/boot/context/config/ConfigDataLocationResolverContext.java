package org.springframework.boot.context.config;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.properties.bind.Binder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLocationResolverContext.class */
public interface ConfigDataLocationResolverContext {
    Binder getBinder();

    ConfigDataResource getParent();

    ConfigurableBootstrapContext getBootstrapContext();
}
