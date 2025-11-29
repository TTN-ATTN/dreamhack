package org.apache.tomcat.util.http.fileupload;

import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/FileItemHeaders.class */
public interface FileItemHeaders {
    String getHeader(String str);

    Iterator<String> getHeaders(String str);

    Iterator<String> getHeaderNames();
}
