package org.springframework.core.io.buffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/buffer/PooledDataBuffer.class */
public interface PooledDataBuffer extends DataBuffer {
    boolean isAllocated();

    PooledDataBuffer retain();

    PooledDataBuffer touch(Object hint);

    boolean release();
}
