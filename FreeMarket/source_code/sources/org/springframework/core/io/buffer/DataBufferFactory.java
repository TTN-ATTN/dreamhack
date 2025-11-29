package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/buffer/DataBufferFactory.class */
public interface DataBufferFactory {
    DataBuffer allocateBuffer();

    DataBuffer allocateBuffer(int initialCapacity);

    DataBuffer wrap(ByteBuffer byteBuffer);

    DataBuffer wrap(byte[] bytes);

    DataBuffer join(List<? extends DataBuffer> dataBuffers);
}
