package org.springframework.core.env;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/PropertySources.class */
public interface PropertySources extends Iterable<PropertySource<?>> {
    boolean contains(String name);

    @Nullable
    PropertySource<?> get(String name);

    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
