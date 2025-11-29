package org.apache.tomcat.util.http.fileupload.util;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/util/Closeable.class */
public interface Closeable {
    void close() throws IOException;

    boolean isClosed() throws IOException;
}
