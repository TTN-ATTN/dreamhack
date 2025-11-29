package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NestedIOException;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/io/AbstractResource.class */
public abstract class AbstractResource implements Resource {
    @Override // org.springframework.core.io.Resource
    public boolean exists() {
        if (isFile()) {
            try {
                return getFile().exists();
            } catch (IOException ex) {
                Log logger = LogFactory.getLog(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not retrieve File for existence check of " + getDescription(), ex);
                }
            }
        }
        try {
            getInputStream().close();
            return true;
        } catch (Throwable ex2) {
            Log logger2 = LogFactory.getLog(getClass());
            if (logger2.isDebugEnabled()) {
                logger2.debug("Could not retrieve InputStream for existence check of " + getDescription(), ex2);
                return false;
            }
            return false;
        }
    }

    @Override // org.springframework.core.io.Resource
    public boolean isReadable() {
        return exists();
    }

    @Override // org.springframework.core.io.Resource
    public boolean isOpen() {
        return false;
    }

    @Override // org.springframework.core.io.Resource
    public boolean isFile() {
        return false;
    }

    @Override // org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override // org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }

    @Override // org.springframework.core.io.Resource
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    @Override // org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    @Override // org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buf = new byte[256];
            while (true) {
                int read = is.read(buf);
                if (read == -1) {
                    break;
                }
                size += read;
            }
            return size;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Log logger = LogFactory.getLog(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not close content-length InputStream for " + getDescription(), ex);
                }
            }
        }
    }

    @Override // org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        File fileToCheck = getFileForLastModifiedCheck();
        long lastModified = fileToCheck.lastModified();
        if (lastModified == 0 && !fileToCheck.exists()) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its last-modified timestamp");
        }
        return lastModified;
    }

    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    @Override // org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    @Override // org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return null;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof Resource) && ((Resource) other).getDescription().equals(getDescription()));
    }

    public int hashCode() {
        return getDescription().hashCode();
    }

    public String toString() {
        return getDescription();
    }
}
