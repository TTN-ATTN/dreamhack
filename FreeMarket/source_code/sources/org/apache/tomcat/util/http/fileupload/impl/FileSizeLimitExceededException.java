package org.apache.tomcat.util.http.fileupload.impl;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/impl/FileSizeLimitExceededException.class */
public class FileSizeLimitExceededException extends SizeException {
    private static final long serialVersionUID = 8150776562029630058L;
    private String fileName;
    private String fieldName;

    public FileSizeLimitExceededException(String message, long actual, long permitted) {
        super(message, actual, permitted);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String pFileName) {
        this.fileName = pFileName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String pFieldName) {
        this.fieldName = pFieldName;
    }
}
