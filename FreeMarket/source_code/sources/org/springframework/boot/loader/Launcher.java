package org.springframework.boot.loader;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/Launcher.class */
public abstract class Launcher {
    private static final String JAR_MODE_LAUNCHER = "org.springframework.boot.loader.jarmode.JarModeLauncher";

    protected abstract String getMainClass() throws Exception;

    protected void launch(String[] args) throws Exception {
        if (!isExploded()) {
            JarFile.registerUrlProtocolHandler();
        }
        ClassLoader classLoader = createClassLoader(getClassPathArchivesIterator());
        String jarMode = System.getProperty("jarmode");
        String launchClass = (jarMode == null || jarMode.isEmpty()) ? getMainClass() : JAR_MODE_LAUNCHER;
        launch(args, launchClass, classLoader);
    }

    @Deprecated
    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
        return createClassLoader(archives.iterator());
    }

    protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(50);
        while (archives.hasNext()) {
            urls.add(archives.next().getUrl());
        }
        return createClassLoader((URL[]) urls.toArray(new URL[0]));
    }

    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new LaunchedURLClassLoader(isExploded(), getArchive(), urls, getClass().getClassLoader());
    }

    protected void launch(String[] args, String launchClass, ClassLoader classLoader) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        createMainMethodRunner(launchClass, args, classLoader).run();
    }

    protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
        return new MainMethodRunner(mainClass, args);
    }

    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        return getClassPathArchives().iterator();
    }

    @Deprecated
    protected List<Archive> getClassPathArchives() throws Exception {
        throw new IllegalStateException("Unexpected call to getClassPathArchives()");
    }

    protected final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource != null ? codeSource.getLocation().toURI() : null;
        String path = location != null ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (root.exists()) {
            return root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root);
        }
        throw new IllegalStateException("Unable to determine code source archive from " + root);
    }

    protected boolean isExploded() {
        return false;
    }

    protected Archive getArchive() {
        return null;
    }
}
