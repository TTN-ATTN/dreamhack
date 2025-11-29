package org.springframework.web.servlet.view;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/view/DefaultRequestToViewNameTranslator.class */
public class DefaultRequestToViewNameTranslator implements RequestToViewNameTranslator {
    private static final String SLASH = "/";
    private String prefix = "";
    private String suffix = "";
    private String separator = "/";
    private boolean stripLeadingSlash = true;
    private boolean stripTrailingSlash = true;
    private boolean stripExtension = true;

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setStripLeadingSlash(boolean stripLeadingSlash) {
        this.stripLeadingSlash = stripLeadingSlash;
    }

    public void setStripTrailingSlash(boolean stripTrailingSlash) {
        this.stripTrailingSlash = stripTrailingSlash;
    }

    public void setStripExtension(boolean stripExtension) {
        this.stripExtension = stripExtension;
    }

    @Deprecated
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
    }

    @Deprecated
    public void setUrlDecode(boolean urlDecode) {
    }

    @Deprecated
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
    }

    @Deprecated
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    }

    @Override // org.springframework.web.servlet.RequestToViewNameTranslator
    public String getViewName(HttpServletRequest request) {
        String path = ServletRequestPathUtils.getCachedPathValue(request);
        return this.prefix + transformPath(path) + this.suffix;
    }

    @Nullable
    protected String transformPath(String lookupPath) {
        String path = lookupPath;
        if (this.stripLeadingSlash && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (this.stripTrailingSlash && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (this.stripExtension) {
            path = StringUtils.stripFilenameExtension(path);
        }
        if (!"/".equals(this.separator)) {
            path = StringUtils.replace(path, "/", this.separator);
        }
        return path;
    }
}
