package org.springframework.boot.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Enumeration;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/LaunchedURLClassLoader.class */
public class LaunchedURLClassLoader extends URLClassLoader {
    private static final int BUFFER_SIZE = 4096;
    private final boolean exploded;
    private final Archive rootArchive;
    private final Object packageLock;
    private volatile DefinePackageCallType definePackageCallType;

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/LaunchedURLClassLoader$DefinePackageCallType.class */
    private enum DefinePackageCallType {
        MANIFEST,
        ATTRIBUTES
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public LaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
        this(false, urls, parent);
    }

    public LaunchedURLClassLoader(boolean exploded, URL[] urls, ClassLoader parent) {
        this(exploded, null, urls, parent);
    }

    public LaunchedURLClassLoader(boolean exploded, Archive rootArchive, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.packageLock = new Object();
        this.exploded = exploded;
        this.rootArchive = rootArchive;
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public URL findResource(String name) {
        if (this.exploded) {
            return super.findResource(name);
        }
        Handler.setUseFastConnectionExceptions(true);
        try {
            URL urlFindResource = super.findResource(name);
            Handler.setUseFastConnectionExceptions(false);
            return urlFindResource;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        if (this.exploded) {
            return super.findResources(name);
        }
        Handler.setUseFastConnectionExceptions(true);
        try {
            UseFastConnectionExceptionsEnumeration useFastConnectionExceptionsEnumeration = new UseFastConnectionExceptionsEnumeration(super.findResources(name));
            Handler.setUseFastConnectionExceptions(false);
            return useFastConnectionExceptionsEnumeration;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    @Override // java.lang.ClassLoader
    protected Class<?> loadClass(String name, boolean resolve) throws IOException, ClassNotFoundException {
        if (name.startsWith("org.springframework.boot.loader.jarmode.")) {
            try {
                Class<?> result = loadClassInLaunchedClassLoader(name);
                if (resolve) {
                    resolveClass(result);
                }
                return result;
            } catch (ClassNotFoundException e) {
            }
        }
        if (this.exploded) {
            return super.loadClass(name, resolve);
        }
        Handler.setUseFastConnectionExceptions(true);
        try {
            try {
                definePackageIfNecessary(name);
            } catch (IllegalArgumentException e2) {
                if (getPackage(name) == null) {
                    throw new AssertionError("Package " + name + " has already been defined but it could not be found");
                }
            }
            Class<?> clsLoadClass = super.loadClass(name, resolve);
            Handler.setUseFastConnectionExceptions(false);
            return clsLoadClass;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    private Class<?> loadClassInLaunchedClassLoader(String name) throws ClassNotFoundException, IOException {
        String internalName = name.replace('.', '/') + ClassUtils.CLASS_FILE_SUFFIX;
        InputStream inputStream = getParent().getResourceAsStream(internalName);
        if (inputStream == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                while (true) {
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    } else {
                        inputStream.close();
                        byte[] bytes = outputStream.toByteArray();
                        Class<?> definedClass = defineClass(name, bytes, 0, bytes.length);
                        definePackageIfNecessary(name);
                        inputStream.close();
                        return definedClass;
                    }
                }
            } catch (Throwable th) {
                inputStream.close();
                throw th;
            }
        } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }

    private void definePackageIfNecessary(String className) throws PrivilegedActionException {
        int lastDot = className.lastIndexOf(46);
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (getPackage(packageName) == null) {
                try {
                    definePackage(className, packageName);
                } catch (IllegalArgumentException e) {
                    if (getPackage(packageName) == null) {
                        throw new AssertionError("Package " + packageName + " has already been defined but it could not be found");
                    }
                }
            }
        }
    }

    private void definePackage(String className, String packageName) throws PrivilegedActionException {
        try {
            AccessController.doPrivileged(() -> {
                String packageEntryName = packageName.replace('.', '/') + "/";
                String classEntryName = className.replace('.', '/') + ClassUtils.CLASS_FILE_SUFFIX;
                for (URL url : getURLs()) {
                    try {
                        URLConnection connection = url.openConnection();
                        if (connection instanceof JarURLConnection) {
                            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                            if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null && jarFile.getManifest() != null) {
                                definePackage(packageName, jarFile.getManifest(), url);
                                return null;
                            }
                        } else {
                            continue;
                        }
                    } catch (IOException e) {
                    }
                }
                return null;
            }, AccessController.getContext());
        } catch (PrivilegedActionException e) {
        }
    }

    @Override // java.net.URLClassLoader
    protected Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
        Package r0;
        if (!this.exploded) {
            return super.definePackage(name, man, url);
        }
        synchronized (this.packageLock) {
            r0 = (Package) doDefinePackage(DefinePackageCallType.MANIFEST, () -> {
                return super.definePackage(name, man, url);
            });
        }
        return r0;
    }

    @Override // java.lang.ClassLoader
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        Manifest manifest;
        if (!this.exploded) {
            return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
        }
        synchronized (this.packageLock) {
            if (this.definePackageCallType == null && (manifest = getManifest(this.rootArchive)) != null) {
                return definePackage(name, manifest, sealBase);
            }
            return (Package) doDefinePackage(DefinePackageCallType.ATTRIBUTES, () -> {
                return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
            });
        }
    }

    private Manifest getManifest(Archive archive) {
        if (archive == null) {
            return null;
        }
        try {
            return archive.getManifest();
        } catch (IOException e) {
            return null;
        }
    }

    private <T> T doDefinePackage(DefinePackageCallType type, Supplier<T> call) {
        DefinePackageCallType existingType = this.definePackageCallType;
        try {
            this.definePackageCallType = type;
            T t = call.get();
            this.definePackageCallType = existingType;
            return t;
        } catch (Throwable th) {
            this.definePackageCallType = existingType;
            throw th;
        }
    }

    public void clearCache() throws IOException {
        if (this.exploded) {
            return;
        }
        for (URL url : getURLs()) {
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    clearCache(connection);
                }
            } catch (IOException e) {
            }
        }
    }

    private void clearCache(URLConnection connection) throws IOException {
        Object jarFile = ((JarURLConnection) connection).getJarFile();
        if (jarFile instanceof org.springframework.boot.loader.jar.JarFile) {
            ((org.springframework.boot.loader.jar.JarFile) jarFile).clearCache();
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/LaunchedURLClassLoader$UseFastConnectionExceptionsEnumeration.class */
    private static class UseFastConnectionExceptionsEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> delegate;

        UseFastConnectionExceptionsEnumeration(Enumeration<URL> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            Handler.setUseFastConnectionExceptions(true);
            try {
                boolean zHasMoreElements = this.delegate.hasMoreElements();
                Handler.setUseFastConnectionExceptions(false);
                return zHasMoreElements;
            } catch (Throwable th) {
                Handler.setUseFastConnectionExceptions(false);
                throw th;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public URL nextElement() {
            Handler.setUseFastConnectionExceptions(true);
            try {
                URL urlNextElement = this.delegate.nextElement();
                Handler.setUseFastConnectionExceptions(false);
                return urlNextElement;
            } catch (Throwable th) {
                Handler.setUseFastConnectionExceptions(false);
                throw th;
            }
        }
    }
}
