package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.data.RandomAccessDataFile;
import org.springframework.boot.loader.jar.AbstractJarFile;
import org.springframework.util.ResourceUtils;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFile.class */
public class JarFile extends AbstractJarFile implements Iterable<java.util.jar.JarEntry> {
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
    private static final String HANDLERS_PACKAGE = "org.springframework.boot.loader";
    private static final AsciiBytes META_INF = new AsciiBytes("META-INF/");
    private static final AsciiBytes SIGNATURE_FILE_EXTENSION = new AsciiBytes(".SF");
    private static final String READ_ACTION = "read";
    private final RandomAccessDataFile rootFile;
    private final String pathFromRoot;
    private final RandomAccessData data;
    private final AbstractJarFile.JarFileType type;
    private URL url;
    private String urlString;
    private JarFileEntries entries;
    private Supplier<Manifest> manifestSupplier;
    private SoftReference<Manifest> manifest;
    private boolean signed;
    private String comment;
    private volatile boolean closed;
    private volatile JarFileWrapper wrapper;

    public JarFile(File file) throws IOException {
        this(new RandomAccessDataFile(file));
    }

    JarFile(RandomAccessDataFile file) throws IOException {
        this(file, "", file, AbstractJarFile.JarFileType.DIRECT);
    }

    private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, AbstractJarFile.JarFileType type) throws IOException {
        this(rootFile, pathFromRoot, data, null, type, null);
    }

    private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, JarEntryFilter filter, AbstractJarFile.JarFileType type, Supplier<Manifest> manifestSupplier) throws IOException {
        super(rootFile.getFile());
        if (System.getSecurityManager() == null) {
            super.close();
        }
        this.rootFile = rootFile;
        this.pathFromRoot = pathFromRoot;
        CentralDirectoryParser parser = new CentralDirectoryParser();
        this.entries = (JarFileEntries) parser.addVisitor(new JarFileEntries(this, filter));
        this.type = type;
        parser.addVisitor(centralDirectoryVisitor());
        try {
            this.data = parser.parse(data, filter == null);
            this.manifestSupplier = manifestSupplier != null ? manifestSupplier : () -> {
                try {
                    try {
                        InputStream inputStream = getInputStream(MANIFEST_NAME);
                        Throwable th = null;
                        if (inputStream != null) {
                            Manifest manifest = new Manifest(inputStream);
                            if (inputStream != null) {
                                if (0 != 0) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                            return manifest;
                        }
                        if (inputStream != null) {
                            if (0 != 0) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th3) {
                                    th.addSuppressed(th3);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        return null;
                    } finally {
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException(e);
            };
        } catch (RuntimeException ex) {
            try {
                this.rootFile.close();
                super.close();
            } catch (IOException e) {
            }
            throw ex;
        }
    }

    private CentralDirectoryVisitor centralDirectoryVisitor() {
        return new CentralDirectoryVisitor() { // from class: org.springframework.boot.loader.jar.JarFile.1
            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
                JarFile.this.comment = endRecord.getComment();
            }

            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitFileHeader(CentralDirectoryFileHeader fileHeader, long dataOffset) {
                AsciiBytes name = fileHeader.getName();
                if (name.startsWith(JarFile.META_INF) && name.endsWith(JarFile.SIGNATURE_FILE_EXTENSION)) {
                    JarFile.this.signed = true;
                }
            }

            @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
            public void visitEnd() {
            }
        };
    }

    JarFileWrapper getWrapper() throws IOException {
        JarFileWrapper wrapper = this.wrapper;
        if (wrapper == null) {
            wrapper = new JarFileWrapper(this);
            this.wrapper = wrapper;
        }
        return wrapper;
    }

    @Override // org.springframework.boot.loader.jar.AbstractJarFile
    Permission getPermission() {
        return new FilePermission(this.rootFile.getFile().getPath(), READ_ACTION);
    }

    protected final RandomAccessDataFile getRootJarFile() {
        return this.rootFile;
    }

    RandomAccessData getData() {
        return this.data;
    }

    @Override // java.util.jar.JarFile
    public Manifest getManifest() throws IOException {
        Manifest manifest = this.manifest != null ? this.manifest.get() : null;
        if (manifest == null) {
            try {
                manifest = this.manifestSupplier.get();
                this.manifest = new SoftReference<>(manifest);
            } catch (RuntimeException ex) {
                throw new IOException(ex);
            }
        }
        return manifest;
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public Enumeration<java.util.jar.JarEntry> entries() {
        return new JarEntryEnumeration(this.entries.iterator());
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public Stream<java.util.jar.JarEntry> stream() {
        Spliterator<java.util.jar.JarEntry> spliterator = Spliterators.spliterator(iterator(), size(), 1297);
        return StreamSupport.stream(spliterator, false);
    }

    @Override // java.lang.Iterable
    public Iterator<java.util.jar.JarEntry> iterator() {
        return this.entries.iterator(this::ensureOpen);
    }

    public JarEntry getJarEntry(CharSequence name) {
        return this.entries.getEntry(name);
    }

    @Override // java.util.jar.JarFile
    public JarEntry getJarEntry(String name) {
        return (JarEntry) getEntry(name);
    }

    public boolean containsEntry(String name) {
        return this.entries.containsEntry(name);
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public ZipEntry getEntry(String name) {
        ensureOpen();
        return this.entries.getEntry(name);
    }

    @Override // org.springframework.boot.loader.jar.AbstractJarFile
    InputStream getInputStream() throws IOException {
        return this.data.getInputStream();
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public synchronized InputStream getInputStream(ZipEntry entry) throws IOException {
        ensureOpen();
        if (entry instanceof JarEntry) {
            return this.entries.getInputStream((JarEntry) entry);
        }
        return getInputStream(entry != null ? entry.getName() : null);
    }

    InputStream getInputStream(String name) throws IOException {
        return this.entries.getInputStream(name);
    }

    public synchronized JarFile getNestedJarFile(ZipEntry entry) throws IOException {
        return getNestedJarFile((JarEntry) entry);
    }

    public synchronized JarFile getNestedJarFile(JarEntry entry) throws IOException {
        try {
            return createJarFileFromEntry(entry);
        } catch (Exception ex) {
            throw new IOException("Unable to open nested jar file '" + entry.getName() + "'", ex);
        }
    }

    private JarFile createJarFileFromEntry(JarEntry entry) throws IOException {
        if (entry.isDirectory()) {
            return createJarFileFromDirectoryEntry(entry);
        }
        return createJarFileFromFileEntry(entry);
    }

    private JarFile createJarFileFromDirectoryEntry(JarEntry entry) throws IOException {
        AsciiBytes name = entry.getAsciiBytesName();
        JarEntryFilter filter = candidate -> {
            if (candidate.startsWith(name) && !candidate.equals(name)) {
                return candidate.substring(name.length());
            }
            return null;
        };
        return new JarFile(this.rootFile, this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR + entry.getName().substring(0, name.length() - 1), this.data, filter, AbstractJarFile.JarFileType.NESTED_DIRECTORY, this.manifestSupplier);
    }

    private JarFile createJarFileFromFileEntry(JarEntry entry) throws IOException {
        if (entry.getMethod() != 0) {
            throw new IllegalStateException("Unable to open nested entry '" + entry.getName() + "'. It has been compressed and nested jar files must be stored without compression. Please check the mechanism used to create your executable jar file");
        }
        RandomAccessData entryData = this.entries.getEntryData(entry.getName());
        return new JarFile(this.rootFile, this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR + entry.getName(), entryData, AbstractJarFile.JarFileType.NESTED_JAR);
    }

    @Override // java.util.zip.ZipFile
    public String getComment() {
        ensureOpen();
        return this.comment;
    }

    @Override // java.util.zip.ZipFile
    public int size() {
        ensureOpen();
        return this.entries.getSize();
    }

    @Override // java.util.zip.ZipFile, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        super.close();
        if (this.type == AbstractJarFile.JarFileType.DIRECT) {
            this.rootFile.close();
        }
        this.closed = true;
    }

    private void ensureOpen() {
        if (this.closed) {
            throw new IllegalStateException("zip file closed");
        }
    }

    boolean isClosed() {
        return this.closed;
    }

    String getUrlString() throws MalformedURLException {
        if (this.urlString == null) {
            this.urlString = getUrl().toString();
        }
        return this.urlString;
    }

    @Override // org.springframework.boot.loader.jar.AbstractJarFile
    public URL getUrl() throws MalformedURLException {
        if (this.url == null) {
            String file = this.rootFile.getFile().toURI() + this.pathFromRoot + ResourceUtils.JAR_URL_SEPARATOR;
            this.url = new URL(ResourceUtils.URL_PROTOCOL_JAR, "", -1, file.replace("file:////", "file://"), new Handler(this));
        }
        return this.url;
    }

    public String toString() {
        return getName();
    }

    @Override // java.util.zip.ZipFile
    public String getName() {
        return this.rootFile.getFile() + this.pathFromRoot;
    }

    boolean isSigned() {
        return this.signed;
    }

    JarEntryCertification getCertification(JarEntry entry) {
        try {
            return this.entries.getCertification(entry);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void clearCache() {
        this.entries.clearCache();
    }

    protected String getPathFromRoot() {
        return this.pathFromRoot;
    }

    @Override // org.springframework.boot.loader.jar.AbstractJarFile
    AbstractJarFile.JarFileType getType() {
        return this.type;
    }

    public static void registerUrlProtocolHandler() {
        Handler.captureJarContextUrl();
        String handlers = System.getProperty(PROTOCOL_HANDLER, "");
        System.setProperty(PROTOCOL_HANDLER, (handlers == null || handlers.isEmpty()) ? HANDLERS_PACKAGE : handlers + "|" + HANDLERS_PACKAGE);
        resetCachedUrlHandlers();
    }

    private static void resetCachedUrlHandlers() {
        try {
            URL.setURLStreamHandlerFactory(null);
        } catch (Error e) {
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFile$JarEntryEnumeration.class */
    private static class JarEntryEnumeration implements Enumeration<java.util.jar.JarEntry> {
        private final Iterator<JarEntry> iterator;

        JarEntryEnumeration(Iterator<JarEntry> iterator) {
            this.iterator = iterator;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public java.util.jar.JarEntry nextElement() {
            return this.iterator.next();
        }
    }
}
