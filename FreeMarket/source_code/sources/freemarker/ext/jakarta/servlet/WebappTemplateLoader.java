package freemarker.ext.jakarta.servlet;

import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLoaderUtils;
import freemarker.cache.URLTemplateSource;
import freemarker.log.Logger;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/WebappTemplateLoader.class */
public class WebappTemplateLoader implements TemplateLoader {
    private static final Logger LOG = Logger.getLogger("freemarker.cache");
    private final ServletContext servletContext;
    private final String subdirPath;
    private Boolean urlConnectionUsesCaches;
    private boolean attemptFileAccess;

    public WebappTemplateLoader(ServletContext servletContext) {
        this(servletContext, "/");
    }

    public WebappTemplateLoader(ServletContext servletContext, String subdirPath) {
        this.attemptFileAccess = true;
        NullArgumentException.check(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
        NullArgumentException.check("subdirPath", subdirPath);
        String subdirPath2 = subdirPath.replace('\\', '/');
        subdirPath2 = subdirPath2.endsWith("/") ? subdirPath2 : subdirPath2 + "/";
        this.subdirPath = subdirPath2.startsWith("/") ? subdirPath2 : "/" + subdirPath2;
        this.servletContext = servletContext;
    }

    @Override // freemarker.cache.TemplateLoader
    public Object findTemplateSource(String name) throws IOException {
        String fullPath = this.subdirPath + name;
        if (this.attemptFileAccess) {
            try {
                String realPath = this.servletContext.getRealPath(fullPath);
                if (realPath != null) {
                    File file = new File(realPath);
                    if (file.canRead()) {
                        if (file.isFile()) {
                            return file;
                        }
                    }
                }
            } catch (SecurityException e) {
            }
        }
        try {
            URL url = this.servletContext.getResource(fullPath);
            if (url == null) {
                return null;
            }
            return new URLTemplateSource(url, getURLConnectionUsesCaches());
        } catch (MalformedURLException e2) {
            LOG.warn("Could not retrieve resource " + StringUtil.jQuoteNoXSS(fullPath), e2);
            return null;
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            return ((File) templateSource).lastModified();
        }
        return ((URLTemplateSource) templateSource).lastModified();
    }

    @Override // freemarker.cache.TemplateLoader
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if (templateSource instanceof File) {
            return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
        }
        return new InputStreamReader(((URLTemplateSource) templateSource).getInputStream(), encoding);
    }

    @Override // freemarker.cache.TemplateLoader
    public void closeTemplateSource(Object templateSource) throws IOException {
        if (!(templateSource instanceof File)) {
            ((URLTemplateSource) templateSource).close();
        }
    }

    public Boolean getURLConnectionUsesCaches() {
        return this.urlConnectionUsesCaches;
    }

    public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
        this.urlConnectionUsesCaches = urlConnectionUsesCaches;
    }

    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this) + "(subdirPath=" + StringUtil.jQuote(this.subdirPath) + ", servletContext={contextPath=" + StringUtil.jQuote(getContextPath()) + ", displayName=" + StringUtil.jQuote(this.servletContext.getServletContextName()) + "})";
    }

    private String getContextPath() {
        try {
            Method m = this.servletContext.getClass().getMethod("getContextPath", CollectionUtils.EMPTY_CLASS_ARRAY);
            return (String) m.invoke(this.servletContext, CollectionUtils.EMPTY_OBJECT_ARRAY);
        } catch (Throwable th) {
            return "[can't query before Serlvet 2.5]";
        }
    }

    public boolean getAttemptFileAccess() {
        return this.attemptFileAccess;
    }

    public void setAttemptFileAccess(boolean attemptLoadingFromFile) {
        this.attemptFileAccess = attemptLoadingFromFile;
    }
}
