package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.compat.JreCompat;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/scan/AbstractInputStreamJar.class */
public abstract class AbstractInputStreamJar implements Jar {
    private final URL jarFileURL;
    private NonClosingJarInputStream jarInputStream = null;
    private JarEntry entry = null;
    private Boolean multiRelease = null;
    private Map<String, String> mrMap = null;

    protected abstract NonClosingJarInputStream createJarInputStream() throws IOException;

    public AbstractInputStreamJar(URL jarFileUrl) {
        this.jarFileURL = jarFileUrl;
    }

    @Override // org.apache.tomcat.Jar
    public URL getJarFileURL() {
        return this.jarFileURL;
    }

    @Override // org.apache.tomcat.Jar
    public void nextEntry() throws NumberFormatException {
        if (this.jarInputStream == null) {
            try {
                reset();
            } catch (IOException e) {
                this.entry = null;
                return;
            }
        }
        try {
            this.entry = this.jarInputStream.getNextJarEntry();
            if (this.multiRelease.booleanValue()) {
                while (this.entry != null && (this.mrMap.containsKey(this.entry.getName()) || (this.entry.getName().startsWith("META-INF/versions/") && !this.mrMap.containsValue(this.entry.getName())))) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            } else {
                while (this.entry != null && this.entry.getName().startsWith("META-INF/versions/")) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            }
        } catch (IOException e2) {
            this.entry = null;
        }
    }

    @Override // org.apache.tomcat.Jar
    public String getEntryName() {
        if (this.entry == null) {
            return null;
        }
        return this.entry.getName();
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getEntryInputStream() throws IOException {
        return this.jarInputStream;
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getInputStream(String name) throws NumberFormatException, IOException {
        gotoEntry(name);
        if (this.entry == null) {
            return null;
        }
        this.entry = null;
        return this.jarInputStream;
    }

    @Override // org.apache.tomcat.Jar
    public long getLastModified(String name) throws NumberFormatException, IOException {
        gotoEntry(name);
        if (this.entry == null) {
            return -1L;
        }
        return this.entry.getTime();
    }

    @Override // org.apache.tomcat.Jar
    public boolean exists(String name) throws NumberFormatException, IOException {
        gotoEntry(name);
        return this.entry != null;
    }

    @Override // org.apache.tomcat.Jar
    public String getURL(String entry) {
        return ResourceUtils.JAR_URL_PREFIX + getJarFileURL().toExternalForm() + ResourceUtils.JAR_URL_SEPARATOR + entry;
    }

    @Override // org.apache.tomcat.Jar
    public Manifest getManifest() throws NumberFormatException, IOException {
        reset();
        return this.jarInputStream.getManifest();
    }

    @Override // org.apache.tomcat.Jar
    public void reset() throws NumberFormatException, IOException {
        Manifest manifest;
        String mrValue;
        closeStream();
        this.entry = null;
        this.jarInputStream = createJarInputStream();
        if (this.multiRelease == null) {
            if (!JreCompat.isJre9Available() || (manifest = this.jarInputStream.getManifest()) == null || (mrValue = manifest.getMainAttributes().getValue("Multi-Release")) == null) {
                this.multiRelease = Boolean.FALSE;
            } else {
                this.multiRelease = Boolean.valueOf(mrValue);
            }
            if (this.multiRelease.booleanValue() && this.mrMap == null) {
                populateMrMap();
            }
        }
    }

    protected void closeStream() {
        if (this.jarInputStream != null) {
            try {
                this.jarInputStream.reallyClose();
            } catch (IOException e) {
            }
        }
    }

    private void gotoEntry(String name) throws NumberFormatException, IOException {
        boolean needsReset = true;
        if (this.multiRelease == null) {
            reset();
            needsReset = false;
        }
        if (this.multiRelease.booleanValue()) {
            String mrName = this.mrMap.get(name);
            if (mrName != null) {
                name = mrName;
            }
        } else if (name.startsWith("META-INF/versions/")) {
            this.entry = null;
            return;
        }
        if (this.entry != null && name.equals(this.entry.getName())) {
            return;
        }
        if (needsReset) {
            reset();
        }
        JarEntry nextJarEntry = this.jarInputStream.getNextJarEntry();
        while (true) {
            JarEntry jarEntry = nextJarEntry;
            if (jarEntry != null) {
                if (name.equals(jarEntry.getName())) {
                    this.entry = jarEntry;
                    return;
                }
                nextJarEntry = this.jarInputStream.getNextJarEntry();
            } else {
                return;
            }
        }
    }

    private void populateMrMap() throws NumberFormatException, IOException {
        int i;
        int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        Map<String, Integer> mrVersions = new HashMap<>();
        JarEntry nextJarEntry = this.jarInputStream.getNextJarEntry();
        while (true) {
            JarEntry jarEntry = nextJarEntry;
            if (jarEntry == null) {
                break;
            }
            String name = jarEntry.getName();
            if (name.startsWith("META-INF/versions/") && name.endsWith(ClassUtils.CLASS_FILE_SUFFIX) && (i = name.indexOf(47, 18)) > 0) {
                String baseName = name.substring(i + 1);
                int version = Integer.parseInt(name.substring(18, i));
                if (version <= targetVersion) {
                    Integer mappedVersion = mrVersions.get(baseName);
                    if (mappedVersion == null) {
                        mrVersions.put(baseName, Integer.valueOf(version));
                    } else if (version > mappedVersion.intValue()) {
                        mrVersions.put(baseName, Integer.valueOf(version));
                    }
                }
            }
            nextJarEntry = this.jarInputStream.getNextJarEntry();
        }
        this.mrMap = new HashMap();
        for (Map.Entry<String, Integer> mrVersion : mrVersions.entrySet()) {
            this.mrMap.put(mrVersion.getKey(), "META-INF/versions/" + mrVersion.getValue().toString() + "/" + mrVersion.getKey());
        }
        closeStream();
        this.jarInputStream = createJarInputStream();
    }
}
