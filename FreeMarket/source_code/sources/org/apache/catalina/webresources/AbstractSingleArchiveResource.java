package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.catalina.webresources.AbstractArchiveResource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/webresources/AbstractSingleArchiveResource.class */
public abstract class AbstractSingleArchiveResource extends AbstractArchiveResource {
    protected AbstractSingleArchiveResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String codeBaseUrl) {
        super(archiveResourceSet, webAppPath, baseUrl, jarEntry, codeBaseUrl);
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResource
    protected AbstractArchiveResource.JarInputStreamWrapper getJarInputStreamWrapper() throws IOException {
        JarFile jarFile = null;
        try {
            jarFile = getArchiveResourceSet().openJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(getResource().getName());
            InputStream is = jarFile.getInputStream(jarEntry);
            return new AbstractArchiveResource.JarInputStreamWrapper(jarEntry, is);
        } catch (IOException e) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("jarResource.getInputStreamFail", getResource().getName(), getBaseUrl()), e);
            }
            if (jarFile != null) {
                getArchiveResourceSet().closeJarFile();
                return null;
            }
            return null;
        }
    }
}
