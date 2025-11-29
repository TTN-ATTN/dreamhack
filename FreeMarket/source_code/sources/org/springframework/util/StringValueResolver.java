package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/StringValueResolver.class */
public interface StringValueResolver {
    @Nullable
    String resolveStringValue(String strVal);
}
