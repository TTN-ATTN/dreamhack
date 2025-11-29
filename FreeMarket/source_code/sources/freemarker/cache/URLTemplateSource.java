package freemarker.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/URLTemplateSource.class */
public class URLTemplateSource {
    private final URL url;
    private URLConnection conn;
    private InputStream inputStream;
    private Boolean useCaches;

    public URLTemplateSource(URL url, Boolean useCaches) throws IOException {
        this.url = url;
        this.conn = url.openConnection();
        this.useCaches = useCaches;
        if (useCaches != null) {
            this.conn.setUseCaches(useCaches.booleanValue());
        }
    }

    public boolean equals(Object o) {
        if (o instanceof URLTemplateSource) {
            return this.url.equals(((URLTemplateSource) o).url);
        }
        return false;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url.toString();
    }

    public long lastModified() throws IOException {
        if (this.conn instanceof JarURLConnection) {
            URL jarURL = ((JarURLConnection) this.conn).getJarFileURL();
            if (jarURL.getProtocol().equals("file")) {
                return new File(jarURL.getFile()).lastModified();
            }
            URLConnection jarConn = null;
            try {
                jarConn = jarURL.openConnection();
                long lastModified = jarConn.getLastModified();
                if (jarConn != null) {
                    try {
                        jarConn.getInputStream().close();
                    } catch (IOException e) {
                    }
                }
                return lastModified;
            } catch (IOException e2) {
                if (jarConn != null) {
                    try {
                        jarConn.getInputStream().close();
                    } catch (IOException e3) {
                        return -1L;
                    }
                }
                return -1L;
            } catch (Throwable th) {
                if (jarConn != null) {
                    try {
                        jarConn.getInputStream().close();
                    } catch (IOException e4) {
                        throw th;
                    }
                }
                throw th;
            }
        }
        long lastModified2 = this.conn.getLastModified();
        if (lastModified2 == -1 && this.url.getProtocol().equals("file")) {
            return new File(this.url.getFile()).lastModified();
        }
        return lastModified2;
    }

    public InputStream getInputStream() throws IOException {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException e) {
            }
            this.conn = this.url.openConnection();
        }
        this.inputStream = this.conn.getInputStream();
        return this.inputStream;
    }

    public void close() throws IOException {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            } else {
                this.conn.getInputStream().close();
            }
        } finally {
            this.inputStream = null;
            this.conn = null;
        }
    }

    Boolean getUseCaches() {
        return this.useCaches;
    }

    void setUseCaches(boolean useCaches) {
        if (this.conn != null) {
            this.conn.setUseCaches(useCaches);
            this.useCaches = Boolean.valueOf(useCaches);
        }
    }
}
