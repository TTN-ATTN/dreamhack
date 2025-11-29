package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/Handler.class */
public class Handler extends URLStreamHandler {
    private static final String JAR_PROTOCOL = "jar:";
    private static final String FILE_PROTOCOL = "file:";
    private static final String TOMCAT_WARFILE_PROTOCOL = "war:file:";
    private static final String SEPARATOR = "!/";
    private static final String PARENT_DIR = "/../";
    private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
    private static URL jarContextUrl;
    private final JarFile jarFile;
    private URLStreamHandler fallbackHandler;
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("!/", 16);
    private static final String CURRENT_DIR = "/./";
    private static final Pattern CURRENT_DIR_PATTERN = Pattern.compile(CURRENT_DIR, 16);
    private static final String[] FALLBACK_HANDLERS = {"sun.net.www.protocol.jar.Handler"};
    private static SoftReference<Map<File, JarFile>> rootFileCache = new SoftReference<>(null);

    public Handler() {
        this(null);
    }

    public Handler(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL url) throws IOException {
        if (this.jarFile != null && isUrlInJarFile(url, this.jarFile)) {
            return JarURLConnection.get(url, this.jarFile);
        }
        try {
            return JarURLConnection.get(url, getRootJarFileFromUrl(url));
        } catch (Exception ex) {
            return openFallbackConnection(url, ex);
        }
    }

    private boolean isUrlInJarFile(URL url, JarFile jarFile) throws MalformedURLException {
        return url.getPath().startsWith(jarFile.getUrl().getPath()) && url.toString().startsWith(jarFile.getUrlString());
    }

    private URLConnection openFallbackConnection(URL url, Exception reason) throws IOException {
        try {
            URLConnection connection = openFallbackTomcatConnection(url);
            URLConnection connection2 = connection != null ? connection : openFallbackContextConnection(url);
            return connection2 != null ? connection2 : openFallbackHandlerConnection(url);
        } catch (Exception ex) {
            if (reason instanceof IOException) {
                log(false, "Unable to open fallback handler", ex);
                throw ((IOException) reason);
            }
            log(true, "Unable to open fallback handler", ex);
            if (reason instanceof RuntimeException) {
                throw ((RuntimeException) reason);
            }
            throw new IllegalStateException(reason);
        }
    }

    private URLConnection openFallbackTomcatConnection(URL url) throws IOException {
        String file = url.getFile();
        if (isTomcatWarUrl(file)) {
            try {
                URLConnection connection = openConnection(new URL("jar:file:" + file.substring(TOMCAT_WARFILE_PROTOCOL.length()).replaceFirst("\\*/", "!/")));
                connection.getInputStream().close();
                return connection;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isTomcatWarUrl(String file) throws IOException {
        if (file.startsWith(TOMCAT_WARFILE_PROTOCOL) || !file.contains(ResourceUtils.WAR_URL_SEPARATOR)) {
            try {
                URLConnection connection = new URL(file).openConnection();
                if (connection.getClass().getName().startsWith("org.apache.catalina")) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private URLConnection openFallbackContextConnection(URL url) {
        try {
            if (jarContextUrl != null) {
                return new URL(jarContextUrl, url.toExternalForm()).openConnection();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private URLConnection openFallbackHandlerConnection(URL url) throws Exception {
        URLStreamHandler fallbackHandler = getFallbackHandler();
        return new URL((URL) null, url.toExternalForm(), fallbackHandler).openConnection();
    }

    private URLStreamHandler getFallbackHandler() throws ClassNotFoundException {
        if (this.fallbackHandler != null) {
            return this.fallbackHandler;
        }
        for (String handlerClassName : FALLBACK_HANDLERS) {
            try {
                Class<?> handlerClass = Class.forName(handlerClassName);
                this.fallbackHandler = (URLStreamHandler) handlerClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                return this.fallbackHandler;
            } catch (Exception e) {
            }
        }
        throw new IllegalStateException("Unable to find fallback handler");
    }

    private void log(boolean warning, String message, Exception cause) {
        try {
            Level level = warning ? Level.WARNING : Level.FINEST;
            Logger.getLogger(getClass().getName()).log(level, message, (Throwable) cause);
        } catch (Exception e) {
            if (warning) {
                System.err.println("WARNING: " + message);
            }
        }
    }

    @Override // java.net.URLStreamHandler
    protected void parseURL(URL context, String spec, int start, int limit) {
        if (spec.regionMatches(true, 0, "jar:", 0, "jar:".length())) {
            setFile(context, getFileFromSpec(spec.substring(start, limit)));
        } else {
            setFile(context, getFileFromContext(context, spec.substring(start, limit)));
        }
    }

    private String getFileFromSpec(String spec) {
        int separatorIndex = spec.lastIndexOf("!/");
        if (separatorIndex == -1) {
            throw new IllegalArgumentException("No !/ in spec '" + spec + "'");
        }
        try {
            new URL(spec.substring(0, separatorIndex));
            return spec;
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid spec URL '" + spec + "'", ex);
        }
    }

    private String getFileFromContext(URL context, String spec) {
        String file = context.getFile();
        if (spec.startsWith("/")) {
            return trimToJarRoot(file) + "!/" + spec.substring(1);
        }
        if (file.endsWith("/")) {
            return file + spec;
        }
        int lastSlashIndex = file.lastIndexOf(47);
        if (lastSlashIndex == -1) {
            throw new IllegalArgumentException("No / found in context URL's file '" + file + "'");
        }
        return file.substring(0, lastSlashIndex + 1) + spec;
    }

    private String trimToJarRoot(String file) {
        int lastSeparatorIndex = file.lastIndexOf("!/");
        if (lastSeparatorIndex == -1) {
            throw new IllegalArgumentException("No !/ found in context URL's file '" + file + "'");
        }
        return file.substring(0, lastSeparatorIndex);
    }

    private void setFile(URL context, String file) {
        String path = normalize(file);
        String query = null;
        int queryIndex = path.lastIndexOf(63);
        if (queryIndex != -1) {
            query = path.substring(queryIndex + 1);
            path = path.substring(0, queryIndex);
        }
        setURL(context, "jar:", null, -1, null, null, path, query, context.getRef());
    }

    private String normalize(String file) {
        if (!file.contains(CURRENT_DIR) && !file.contains(PARENT_DIR)) {
            return file;
        }
        int afterLastSeparatorIndex = file.lastIndexOf("!/") + "!/".length();
        String afterSeparator = file.substring(afterLastSeparatorIndex);
        return file.substring(0, afterLastSeparatorIndex) + replaceCurrentDir(replaceParentDir(afterSeparator));
    }

    private String replaceParentDir(String file) {
        while (true) {
            int parentDirIndex = file.indexOf(PARENT_DIR);
            if (parentDirIndex >= 0) {
                int precedingSlashIndex = file.lastIndexOf(47, parentDirIndex - 1);
                if (precedingSlashIndex >= 0) {
                    file = file.substring(0, precedingSlashIndex) + file.substring(parentDirIndex + 3);
                } else {
                    file = file.substring(parentDirIndex + 4);
                }
            } else {
                return file;
            }
        }
    }

    private String replaceCurrentDir(String file) {
        return CURRENT_DIR_PATTERN.matcher(file).replaceAll("/");
    }

    @Override // java.net.URLStreamHandler
    protected int hashCode(URL u) {
        return hashCode(u.getProtocol(), u.getFile());
    }

    private int hashCode(String protocol, String file) {
        int result = protocol != null ? protocol.hashCode() : 0;
        int separatorIndex = file.indexOf("!/");
        if (separatorIndex == -1) {
            return result + file.hashCode();
        }
        String source = file.substring(0, separatorIndex);
        String entry = canonicalize(file.substring(separatorIndex + 2));
        try {
            result += new URL(source).hashCode();
        } catch (MalformedURLException e) {
            result += source.hashCode();
        }
        return result + entry.hashCode();
    }

    @Override // java.net.URLStreamHandler
    protected boolean sameFile(URL u1, URL u2) {
        if (!u1.getProtocol().equals(ResourceUtils.URL_PROTOCOL_JAR) || !u2.getProtocol().equals(ResourceUtils.URL_PROTOCOL_JAR)) {
            return false;
        }
        int separator1 = u1.getFile().indexOf("!/");
        int separator2 = u2.getFile().indexOf("!/");
        if (separator1 == -1 || separator2 == -1) {
            return super.sameFile(u1, u2);
        }
        String nested1 = u1.getFile().substring(separator1 + "!/".length());
        String nested2 = u2.getFile().substring(separator2 + "!/".length());
        if (!nested1.equals(nested2)) {
            String canonical1 = canonicalize(nested1);
            String canonical2 = canonicalize(nested2);
            if (!canonical1.equals(canonical2)) {
                return false;
            }
        }
        String root1 = u1.getFile().substring(0, separator1);
        String root2 = u2.getFile().substring(0, separator2);
        try {
            return super.sameFile(new URL(root1), new URL(root2));
        } catch (MalformedURLException e) {
            return super.sameFile(u1, u2);
        }
    }

    private String canonicalize(String path) {
        return SEPARATOR_PATTERN.matcher(path).replaceAll("/");
    }

    public JarFile getRootJarFileFromUrl(URL url) throws IOException {
        String spec = url.getFile();
        int separatorIndex = spec.indexOf("!/");
        if (separatorIndex == -1) {
            throw new MalformedURLException("Jar URL does not contain !/ separator");
        }
        String name = spec.substring(0, separatorIndex);
        return getRootJarFile(name);
    }

    private JarFile getRootJarFile(String name) throws IOException {
        try {
            if (!name.startsWith("file:")) {
                throw new IllegalStateException("Not a file URL");
            }
            File file = new File(URI.create(name));
            Map<File, JarFile> cache = rootFileCache.get();
            JarFile result = cache != null ? cache.get(file) : null;
            if (result == null) {
                result = new JarFile(file);
                addToRootFileCache(file, result);
            }
            return result;
        } catch (Exception ex) {
            throw new IOException("Unable to open root Jar file '" + name + "'", ex);
        }
    }

    static void addToRootFileCache(File sourceFile, JarFile jarFile) {
        Map<File, JarFile> cache = rootFileCache.get();
        if (cache == null) {
            cache = new ConcurrentHashMap();
            rootFileCache = new SoftReference<>(cache);
        }
        cache.put(sourceFile, jarFile);
    }

    static void captureJarContextUrl() {
        if (canResetCachedUrlHandlers()) {
            String handlers = System.getProperty(PROTOCOL_HANDLER);
            try {
                System.clearProperty(PROTOCOL_HANDLER);
                try {
                    resetCachedUrlHandlers();
                    jarContextUrl = new URL("jar:file:context.jar!/");
                    URLConnection connection = jarContextUrl.openConnection();
                    if (connection instanceof JarURLConnection) {
                        jarContextUrl = null;
                    }
                } catch (Exception e) {
                }
                resetCachedUrlHandlers();
            } finally {
                if (handlers == null) {
                    System.clearProperty(PROTOCOL_HANDLER);
                } else {
                    System.setProperty(PROTOCOL_HANDLER, handlers);
                }
            }
        }
    }

    private static boolean canResetCachedUrlHandlers() {
        try {
            resetCachedUrlHandlers();
            return true;
        } catch (Error e) {
            return false;
        }
    }

    private static void resetCachedUrlHandlers() {
        URL.setURLStreamHandlerFactory(null);
    }

    public static void setUseFastConnectionExceptions(boolean useFastConnectionExceptions) {
        JarURLConnection.setUseFastExceptions(useFastConnectionExceptions);
    }
}
