package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/webresources/WarResourceSet.class */
public class WarResourceSet extends AbstractSingleArchiveResourceSet {
    public WarResourceSet() {
    }

    public WarResourceSet(WebResourceRoot root, String webAppMount, String base) throws IllegalArgumentException {
        super(root, webAppMount, base, "/");
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new WarResource(this, webAppPath, getBaseUrlString(), jarEntry);
    }
}
