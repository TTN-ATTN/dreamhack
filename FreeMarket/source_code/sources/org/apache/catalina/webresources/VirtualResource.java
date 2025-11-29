package org.apache.catalina.webresources;

import org.apache.catalina.WebResourceRoot;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/webresources/VirtualResource.class */
public class VirtualResource extends EmptyResource {
    private final String name;

    public VirtualResource(WebResourceRoot root, String webAppPath, String name) {
        super(root, webAppPath);
        this.name = name;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public boolean isVirtual() {
        return true;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public boolean isDirectory() {
        return true;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public String getName() {
        return this.name;
    }
}
