package org.springframework.boot.logging;

import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/logging/DeferredLogFactory.class */
public interface DeferredLogFactory {
    Log getLog(Supplier<Log> destination);

    default Log getLog(Class<?> destination) {
        return getLog(() -> {
            return LogFactory.getLog((Class<?>) destination);
        });
    }

    default Log getLog(Log destination) {
        return getLog(() -> {
            return destination;
        });
    }
}
