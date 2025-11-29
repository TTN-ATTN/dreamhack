package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/webresources/WarResource.class */
public class WarResource extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog((Class<?>) WarResource.class);

    public WarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, ResourceUtils.WAR_URL_PREFIX + baseUrl + UriUtil.getWarSeparator(), jarEntry, baseUrl);
    }

    @Override // org.apache.catalina.webresources.AbstractResource
    protected Log getLog() {
        return log;
    }
}
