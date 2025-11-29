package org.springframework.core.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/serializer/Serializer.class */
public interface Serializer<T> {
    void serialize(T object, OutputStream outputStream) throws IOException;

    default byte[] serializeToByteArray(T object) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        serialize(object, out);
        return out.toByteArray();
    }
}
