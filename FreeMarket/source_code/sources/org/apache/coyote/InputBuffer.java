package org.apache.coyote;

import java.io.IOException;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/InputBuffer.class */
public interface InputBuffer {
    int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException;

    int available();
}
