package org.springframework.util;

import java.util.UUID;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/IdGenerator.class */
public interface IdGenerator {
    UUID generateId();
}
