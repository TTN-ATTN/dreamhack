package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/InputStreamSource.class */
public interface InputStreamSource {
    InputStream getInputStream() throws IOException;
}
