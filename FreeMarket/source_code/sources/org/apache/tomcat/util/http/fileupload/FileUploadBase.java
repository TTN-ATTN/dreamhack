package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.apache.tomcat.util.http.fileupload.util.FileItemHeadersImpl;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase.class */
public abstract class FileUploadBase {
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    private long sizeMax = -1;
    private long fileSizeMax = -1;
    private long fileCountMax = -1;
    private String headerEncoding;
    private ProgressListener listener;

    public abstract FileItemFactory getFileItemFactory();

    public abstract void setFileItemFactory(FileItemFactory fileItemFactory);

    public static final boolean isMultipartContent(RequestContext ctx) {
        String contentType = ctx.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART);
    }

    public long getSizeMax() {
        return this.sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public long getFileCountMax() {
        return this.fileCountMax;
    }

    public void setFileCountMax(long fileCountMax) {
        this.fileCountMax = fileCountMax;
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    public FileItemIterator getItemIterator(RequestContext ctx) throws IOException {
        try {
            return new FileItemIteratorImpl(this, ctx);
        } catch (FileUploadIOException e) {
            throw ((FileUploadException) e.getCause());
        }
    }

    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = new ArrayList<>();
        boolean successful = false;
        try {
            try {
                try {
                    FileItemIterator iter = getItemIterator(ctx);
                    FileItemFactory fileItemFactory = (FileItemFactory) Objects.requireNonNull(getFileItemFactory(), "No FileItemFactory has been set.");
                    byte[] buffer = new byte[8192];
                    while (iter.hasNext()) {
                        if (items.size() == this.fileCountMax) {
                            throw new FileCountLimitExceededException(ATTACHMENT, getFileCountMax());
                        }
                        FileItemStream item = iter.next();
                        String fileName = item.getName();
                        FileItem fileItem = fileItemFactory.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
                        items.add(fileItem);
                        try {
                            Streams.copy(item.openStream(), fileItem.getOutputStream(), true, buffer);
                            FileItemHeaders fih = item.getHeaders();
                            fileItem.setHeaders(fih);
                        } catch (FileUploadIOException e) {
                            throw ((FileUploadException) e.getCause());
                        } catch (IOException e2) {
                            throw new IOFileUploadException(String.format("Processing of %s request failed. %s", "multipart/form-data", e2.getMessage()), e2);
                        }
                    }
                    successful = true;
                    return items;
                } catch (FileUploadException e3) {
                    throw e3;
                }
            } catch (IOException e4) {
                throw new FileUploadException(e4.getMessage(), e4);
            }
        } finally {
            if (!successful) {
                Iterator<FileItem> it = items.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().delete();
                    } catch (Exception e5) {
                    }
                }
            }
        }
    }

    public Map<String, List<FileItem>> parseParameterMap(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = parseRequest(ctx);
        Map<String, List<FileItem>> itemsMap = new HashMap<>(items.size());
        for (FileItem fileItem : items) {
            String fieldName = fileItem.getFieldName();
            List<FileItem> mappedItems = itemsMap.computeIfAbsent(fieldName, k -> {
                return new ArrayList();
            });
            mappedItems.add(fileItem);
        }
        return itemsMap;
    }

    public byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary = boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
        return boundary;
    }

    public String getFileName(FileItemHeaders headers) {
        return getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFileName(String pContentDisposition) {
        String fileName = null;
        if (pContentDisposition != null) {
            String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {
                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);
                Map<String, String> params = parser.parse(pContentDisposition, ';');
                if (params.containsKey("filename")) {
                    String fileName2 = params.get("filename");
                    fileName = fileName2 != null ? fileName2.trim() : "";
                }
            }
        }
        return fileName;
    }

    public String getFieldName(FileItemHeaders headers) {
        return getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFieldName(String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }

    public FileItemHeaders getParsedHeaders(String headerPart) {
        char c;
        int len = headerPart.length();
        FileItemHeadersImpl headers = newFileItemHeaders();
        int start = 0;
        while (true) {
            int end = parseEndOfLine(headerPart, start);
            if (start != end) {
                StringBuilder header = new StringBuilder(headerPart.substring(start, end));
                while (true) {
                    start = end + 2;
                    if (start < len) {
                        int nonWs = start;
                        while (nonWs < len && ((c = headerPart.charAt(nonWs)) == ' ' || c == '\t')) {
                            nonWs++;
                        }
                        if (nonWs == start) {
                            break;
                        }
                        end = parseEndOfLine(headerPart, nonWs);
                        header.append(' ').append((CharSequence) headerPart, nonWs, end);
                    }
                }
                parseHeaderLine(headers, header.toString());
            } else {
                return headers;
            }
        }
    }

    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0025, code lost:
    
        throw new java.lang.IllegalStateException("Expected headers to be terminated by an empty line.");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int parseEndOfLine(java.lang.String r5, int r6) {
        /*
            r4 = this;
            r0 = r6
            r7 = r0
        L2:
            r0 = r5
            r1 = 13
            r2 = r7
            int r0 = r0.indexOf(r1, r2)
            r8 = r0
            r0 = r8
            r1 = -1
            if (r0 == r1) goto L1c
            r0 = r8
            r1 = 1
            int r0 = r0 + r1
            r1 = r5
            int r1 = r1.length()
            if (r0 < r1) goto L26
        L1c:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "Expected headers to be terminated by an empty line."
            r1.<init>(r2)
            throw r0
        L26:
            r0 = r5
            r1 = r8
            r2 = 1
            int r1 = r1 + r2
            char r0 = r0.charAt(r1)
            r1 = 10
            if (r0 != r1) goto L36
            r0 = r8
            return r0
        L36:
            r0 = r8
            r1 = 1
            int r0 = r0 + r1
            r7 = r0
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.fileupload.FileUploadBase.parseEndOfLine(java.lang.String, int):int");
    }

    private void parseHeaderLine(FileItemHeadersImpl headers, String header) {
        int colonOffset = header.indexOf(58);
        if (colonOffset == -1) {
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue = header.substring(colonOffset + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

    public ProgressListener getProgressListener() {
        return this.listener;
    }

    public void setProgressListener(ProgressListener pListener) {
        this.listener = pListener;
    }
}
