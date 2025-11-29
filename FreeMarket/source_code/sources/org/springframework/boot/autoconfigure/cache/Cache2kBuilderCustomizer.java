package org.springframework.boot.autoconfigure.cache;

import org.cache2k.Cache2kBuilder;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cache/Cache2kBuilderCustomizer.class */
public interface Cache2kBuilderCustomizer {
    void customize(Cache2kBuilder<?, ?> builder);
}
