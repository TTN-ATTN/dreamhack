package org.springframework.core.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/ValueExtractor.class */
interface ValueExtractor {
    @Nullable
    Object extract(Method attribute, @Nullable Object object);
}
