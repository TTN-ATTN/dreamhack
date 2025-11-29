package org.springframework.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/SimpleIdGenerator.class */
public class SimpleIdGenerator implements IdGenerator {
    private final AtomicLong leastSigBits = new AtomicLong();

    @Override // org.springframework.util.IdGenerator
    public UUID generateId() {
        return new UUID(0L, this.leastSigBits.incrementAndGet());
    }
}
