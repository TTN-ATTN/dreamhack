package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/OutputBuffer.class */
public interface OutputBuffer {
    int doWrite(ByteBuffer byteBuffer) throws IOException;

    long getBytesWritten();
}
