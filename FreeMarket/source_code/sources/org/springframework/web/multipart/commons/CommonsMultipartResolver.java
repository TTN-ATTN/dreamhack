package org.springframework.web.multipart.commons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/multipart/commons/CommonsMultipartResolver.class */
public class CommonsMultipartResolver extends CommonsFileUploadSupport implements MultipartResolver, ServletContextAware {
    private boolean resolveLazily;

    @Nullable
    private Set<String> supportedMethods;

    public CommonsMultipartResolver() {
        this.resolveLazily = false;
    }

    public CommonsMultipartResolver(ServletContext servletContext) {
        this();
        setServletContext(servletContext);
    }

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public void setSupportedMethods(String... supportedMethods) {
        this.supportedMethods = new HashSet(Arrays.asList(supportedMethods));
    }

    @Override // org.springframework.web.multipart.commons.CommonsFileUploadSupport
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory);
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        if (!isUploadTempDirSpecified()) {
            getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
        }
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public boolean isMultipart(HttpServletRequest request) {
        if (this.supportedMethods != null) {
            return this.supportedMethods.contains(request.getMethod()) && FileUploadBase.isMultipartContent(new ServletRequestContext(request));
        }
        return ServletFileUpload.isMultipartContent(request);
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        Assert.notNull(request, "Request must not be null");
        if (this.resolveLazily) {
            return new DefaultMultipartHttpServletRequest(request) { // from class: org.springframework.web.multipart.commons.CommonsMultipartResolver.1
                @Override // org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
                protected void initializeMultipart() throws MultipartException {
                    CommonsFileUploadSupport.MultipartParsingResult parsingResult = CommonsMultipartResolver.this.parseRequest(request);
                    setMultipartFiles(parsingResult.getMultipartFiles());
                    setMultipartParameters(parsingResult.getMultipartParameters());
                    setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
                }
            };
        }
        CommonsFileUploadSupport.MultipartParsingResult parsingResult = parseRequest(request);
        return new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
    }

    protected CommonsFileUploadSupport.MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        String encoding = determineEncoding(request);
        ServletFileUpload servletFileUploadPrepareFileUpload = prepareFileUpload(encoding);
        try {
            List<FileItem> fileItems = servletFileUploadPrepareFileUpload.parseRequest(request);
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(servletFileUploadPrepareFileUpload.getSizeMax(), ex);
        } catch (FileUploadBase.FileSizeLimitExceededException ex2) {
            throw new MaxUploadSizeExceededException(servletFileUploadPrepareFileUpload.getFileSizeMax(), ex2);
        } catch (FileUploadException ex3) {
            throw new MultipartException("Failed to parse multipart servlet request", ex3);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = getDefaultEncoding();
        }
        return encoding;
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest) request).isResolved()) {
            try {
                cleanupFileItems(request.getMultiFileMap());
            } catch (Throwable ex) {
                this.logger.warn("Failed to perform multipart cleanup for servlet request", ex);
            }
        }
    }
}
