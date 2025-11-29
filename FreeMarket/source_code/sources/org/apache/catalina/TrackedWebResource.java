package org.apache.catalina;

import java.io.Closeable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/TrackedWebResource.class */
public interface TrackedWebResource extends Closeable {
    Exception getCreatedBy();

    String getName();
}
