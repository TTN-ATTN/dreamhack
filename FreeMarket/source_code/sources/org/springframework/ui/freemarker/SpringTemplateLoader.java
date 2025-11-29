package org.springframework.ui.freemarker;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/ui/freemarker/SpringTemplateLoader.class */
public class SpringTemplateLoader implements TemplateLoader {
    protected final Log logger = LogFactory.getLog(getClass());
    private final ResourceLoader resourceLoader;
    private final String templateLoaderPath;

    public SpringTemplateLoader(ResourceLoader resourceLoader, String templateLoaderPath) {
        this.resourceLoader = resourceLoader;
        this.templateLoaderPath = templateLoaderPath.endsWith("/") ? templateLoaderPath : templateLoaderPath + "/";
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("SpringTemplateLoader for FreeMarker: using resource loader [" + this.resourceLoader + "] and template loader path [" + this.templateLoaderPath + "]");
        }
    }

    @Override // freemarker.cache.TemplateLoader
    @Nullable
    public Object findTemplateSource(String name) throws IOException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking for FreeMarker template with name [" + name + "]");
        }
        Resource resource = this.resourceLoader.getResource(this.templateLoaderPath + name);
        if (resource.exists()) {
            return resource;
        }
        return null;
    }

    @Override // freemarker.cache.TemplateLoader
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Resource resource = (Resource) templateSource;
        try {
            return new InputStreamReader(resource.getInputStream(), encoding);
        } catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not find FreeMarker template: " + resource);
            }
            throw ex;
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public long getLastModified(Object templateSource) {
        Resource resource = (Resource) templateSource;
        try {
            return resource.lastModified();
        } catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not obtain last-modified timestamp for FreeMarker template in " + resource + ": " + ex);
                return -1L;
            }
            return -1L;
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public void closeTemplateSource(Object templateSource) throws IOException {
    }
}
