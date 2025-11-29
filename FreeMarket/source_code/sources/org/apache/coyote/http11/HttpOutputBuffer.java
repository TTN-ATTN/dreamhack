package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.OutputBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http11/HttpOutputBuffer.class */
public interface HttpOutputBuffer extends OutputBuffer {
    void end() throws IOException;

    void flush() throws IOException;
}
