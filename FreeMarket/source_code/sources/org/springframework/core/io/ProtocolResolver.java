package org.springframework.core.io;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/ProtocolResolver.class */
public interface ProtocolResolver {
    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);
}
