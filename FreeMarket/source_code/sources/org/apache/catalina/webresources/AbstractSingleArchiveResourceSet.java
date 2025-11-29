package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/webresources/AbstractSingleArchiveResourceSet.class */
public abstract class AbstractSingleArchiveResourceSet extends AbstractArchiveResourceSet {
    private volatile Boolean multiRelease;

    public AbstractSingleArchiveResourceSet() {
    }

    public AbstractSingleArchiveResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) throws IllegalArgumentException {
        setRoot(root);
        setWebAppMount(webAppMount);
        setBase(base);
        setInternalPath(internalPath);
        if (getRoot().getState().isAvailable()) {
            try {
                start();
            } catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected Map<String, JarEntry> getArchiveEntries(boolean single) {
        Map<String, JarEntry> map;
        synchronized (this.archiveLock) {
            if (this.archiveEntries == null && !single) {
                this.archiveEntries = new HashMap();
                try {
                    try {
                        JarFile jarFile = openJarFile();
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                        if (jarFile != null) {
                            closeJarFile();
                        }
                    } catch (Throwable th) {
                        if (0 != 0) {
                            closeJarFile();
                        }
                        throw th;
                    }
                } catch (IOException ioe) {
                    this.archiveEntries = null;
                    throw new IllegalStateException(ioe);
                }
            }
            map = this.archiveEntries;
        }
        return map;
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected JarEntry getArchiveEntry(String pathInArchive) {
        JarFile jarFile = null;
        try {
            try {
                jarFile = openJarFile();
                JarEntry jarEntry = jarFile.getJarEntry(pathInArchive);
                if (jarFile != null) {
                    closeJarFile();
                }
                return jarEntry;
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        } catch (Throwable th) {
            if (jarFile != null) {
                closeJarFile();
            }
            throw th;
        }
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected boolean isMultiRelease() {
        if (this.multiRelease == null) {
            synchronized (this.archiveLock) {
                if (this.multiRelease == null) {
                    JarFile jarFile = null;
                    try {
                        try {
                            jarFile = openJarFile();
                            this.multiRelease = Boolean.valueOf(JreCompat.getInstance().jarFileIsMultiRelease(jarFile));
                            if (jarFile != null) {
                                closeJarFile();
                            }
                        } catch (IOException ioe) {
                            throw new IllegalStateException(ioe);
                        }
                    } catch (Throwable th) {
                        if (jarFile != null) {
                            closeJarFile();
                        }
                        throw th;
                    }
                }
            }
        }
        return this.multiRelease.booleanValue();
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
        try {
            JarFile jarFile = JreCompat.getInstance().jarFileNewInstance(getBase());
            try {
                setManifest(jarFile.getManifest());
                if (jarFile != null) {
                    jarFile.close();
                }
                try {
                    setBaseUrl(UriUtil.buildJarSafeUrl(new File(getBase())));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            } finally {
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
}
