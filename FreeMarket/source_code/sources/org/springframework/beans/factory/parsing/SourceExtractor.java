package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/parsing/SourceExtractor.class */
public interface SourceExtractor {
    @Nullable
    Object extractSource(Object obj, @Nullable Resource resource);
}
