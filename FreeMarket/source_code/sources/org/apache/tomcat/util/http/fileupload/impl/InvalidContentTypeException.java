package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/impl/InvalidContentTypeException.class */
public class InvalidContentTypeException extends FileUploadException {
    private static final long serialVersionUID = -9073026332015646668L;

    public InvalidContentTypeException() {
    }

    public InvalidContentTypeException(String message) {
        super(message);
    }

    public InvalidContentTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
