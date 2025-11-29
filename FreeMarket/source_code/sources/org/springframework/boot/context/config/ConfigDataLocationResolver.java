package org.springframework.boot.context.config;

import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.config.ConfigDataResource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLocationResolver.class */
public interface ConfigDataLocationResolver<R extends ConfigDataResource> {
    boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location);

    List<R> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location) throws ConfigDataResourceNotFoundException, ConfigDataLocationNotFoundException;

    default List<R> resolveProfileSpecific(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        return Collections.emptyList();
    }
}
