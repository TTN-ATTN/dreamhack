package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/FileItemIterator.class */
public interface FileItemIterator {
    long getFileSizeMax();

    void setFileSizeMax(long j);

    long getSizeMax();

    void setSizeMax(long j);

    boolean hasNext() throws IOException;

    FileItemStream next() throws IOException;

    List<FileItem> getFileItems() throws IOException;
}
