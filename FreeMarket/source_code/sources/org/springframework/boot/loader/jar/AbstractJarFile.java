package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/AbstractJarFile.class */
abstract class AbstractJarFile extends java.util.jar.JarFile {

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/AbstractJarFile$JarFileType.class */
    enum JarFileType {
        DIRECT,
        NESTED_DIRECTORY,
        NESTED_JAR
    }

    abstract URL getUrl() throws MalformedURLException;

    abstract JarFileType getType();

    abstract Permission getPermission();

    abstract InputStream getInputStream() throws IOException;

    AbstractJarFile(File file) throws IOException {
        super(file);
    }
}
