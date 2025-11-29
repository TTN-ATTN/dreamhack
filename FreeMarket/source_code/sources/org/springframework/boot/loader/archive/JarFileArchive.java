package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.jar.JarFile;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/JarFileArchive.class */
public class JarFileArchive implements Archive {
    private static final String UNPACK_MARKER = "UNPACK:";
    private static final int BUFFER_SIZE = 32768;
    private static final FileAttribute<?>[] NO_FILE_ATTRIBUTES = new FileAttribute[0];
    private static final EnumSet<PosixFilePermission> DIRECTORY_PERMISSIONS = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE);
    private static final EnumSet<PosixFilePermission> FILE_PERMISSIONS = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
    private final JarFile jarFile;
    private URL url;
    private Path tempUnpackDirectory;

    public JarFileArchive(File file) throws IOException {
        this(file, file.toURI().toURL());
    }

    public JarFileArchive(File file, URL url) throws IOException {
        this(new JarFile(file));
        this.url = url;
    }

    public JarFileArchive(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public URL getUrl() throws MalformedURLException {
        if (this.url != null) {
            return this.url;
        }
        return this.jarFile.getUrl();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Iterator<Archive> getNestedArchives(Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) throws IOException {
        return new NestedArchiveIterator(this.jarFile.iterator(), searchFilter, includeFilter);
    }

    @Override // org.springframework.boot.loader.archive.Archive, java.lang.Iterable
    @Deprecated
    public Iterator<Archive.Entry> iterator() {
        return new EntryIterator(this.jarFile.iterator(), null, null);
    }

    @Override // org.springframework.boot.loader.archive.Archive, java.lang.AutoCloseable
    public void close() throws IOException {
        this.jarFile.close();
    }

    protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
        JarEntry jarEntry = ((JarFileEntry) entry).getJarEntry();
        if (jarEntry.getComment().startsWith(UNPACK_MARKER)) {
            return getUnpackedNestedArchive(jarEntry);
        }
        try {
            JarFile jarFile = this.jarFile.getNestedJarFile(jarEntry);
            return new JarFileArchive(jarFile);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get nested archive for entry " + entry.getName(), ex);
        }
    }

    private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
        String name = jarEntry.getName();
        if (name.lastIndexOf(47) != -1) {
            name = name.substring(name.lastIndexOf(47) + 1);
        }
        Path path = getTempUnpackDirectory().resolve(name);
        if (!Files.exists(path, new LinkOption[0]) || Files.size(path) != jarEntry.getSize()) {
            unpack(jarEntry, path);
        }
        return new JarFileArchive(path.toFile(), path.toUri().toURL());
    }

    private Path getTempUnpackDirectory() {
        if (this.tempUnpackDirectory == null) {
            Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"), new String[0]);
            this.tempUnpackDirectory = createUnpackDirectory(tempDirectory);
        }
        return this.tempUnpackDirectory;
    }

    private Path createUnpackDirectory(Path parent) {
        int attempts = 0;
        while (true) {
            int i = attempts;
            attempts++;
            if (i < 1000) {
                String fileName = Paths.get(this.jarFile.getName(), new String[0]).getFileName().toString();
                Path unpackDirectory = parent.resolve(fileName + "-spring-boot-libs-" + UUID.randomUUID());
                try {
                    createDirectory(unpackDirectory);
                    return unpackDirectory;
                } catch (IOException e) {
                }
            } else {
                throw new IllegalStateException("Failed to create unpack directory in directory '" + parent + "'");
            }
        }
    }

    private void unpack(JarEntry entry, Path path) throws IOException {
        createFile(path);
        path.toFile().deleteOnExit();
        InputStream inputStream = this.jarFile.getInputStream(entry);
        Throwable th = null;
        try {
            OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            Throwable th2 = null;
            try {
                try {
                    byte[] buffer = new byte[32768];
                    while (true) {
                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead == -1) {
                            break;
                        } else {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    outputStream.flush();
                    if (outputStream != null) {
                        if (0 != 0) {
                            try {
                                outputStream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            outputStream.close();
                        }
                    }
                    if (inputStream != null) {
                        if (0 == 0) {
                            inputStream.close();
                            return;
                        }
                        try {
                            inputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    }
                } catch (Throwable th5) {
                    th2 = th5;
                    throw th5;
                }
            } catch (Throwable th6) {
                if (outputStream != null) {
                    if (th2 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                        }
                    } else {
                        outputStream.close();
                    }
                }
                throw th6;
            }
        } catch (Throwable th8) {
            if (inputStream != null) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    inputStream.close();
                }
            }
            throw th8;
        }
    }

    private void createDirectory(Path path) throws IOException {
        Files.createDirectory(path, getFileAttributes(path.getFileSystem(), DIRECTORY_PERMISSIONS));
    }

    private void createFile(Path path) throws IOException {
        Files.createFile(path, getFileAttributes(path.getFileSystem(), FILE_PERMISSIONS));
    }

    private FileAttribute<?>[] getFileAttributes(FileSystem fileSystem, EnumSet<PosixFilePermission> ownerReadWrite) {
        if (!fileSystem.supportedFileAttributeViews().contains("posix")) {
            return NO_FILE_ATTRIBUTES;
        }
        return new FileAttribute[]{PosixFilePermissions.asFileAttribute(ownerReadWrite)};
    }

    public String toString() {
        try {
            return getUrl().toString();
        } catch (Exception e) {
            return "jar archive";
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/JarFileArchive$AbstractIterator.class */
    private static abstract class AbstractIterator<T> implements Iterator<T> {
        private final Iterator<JarEntry> iterator;
        private final Archive.EntryFilter searchFilter;
        private final Archive.EntryFilter includeFilter;
        private Archive.Entry current = poll();

        protected abstract T adapt(Archive.Entry entry);

        AbstractIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            this.iterator = iterator;
            this.searchFilter = searchFilter;
            this.includeFilter = includeFilter;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        @Override // java.util.Iterator
        public T next() {
            T result = adapt(this.current);
            this.current = poll();
            return result;
        }

        private Archive.Entry poll() {
            while (this.iterator.hasNext()) {
                JarFileEntry candidate = new JarFileEntry(this.iterator.next());
                if (this.searchFilter == null || this.searchFilter.matches(candidate)) {
                    if (this.includeFilter == null || this.includeFilter.matches(candidate)) {
                        return candidate;
                    }
                }
            }
            return null;
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/JarFileArchive$EntryIterator.class */
    private static class EntryIterator extends AbstractIterator<Archive.Entry> {
        EntryIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            super(iterator, searchFilter, includeFilter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.boot.loader.archive.JarFileArchive.AbstractIterator
        public Archive.Entry adapt(Archive.Entry entry) {
            return entry;
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/JarFileArchive$NestedArchiveIterator.class */
    private class NestedArchiveIterator extends AbstractIterator<Archive> {
        NestedArchiveIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            super(iterator, searchFilter, includeFilter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.boot.loader.archive.JarFileArchive.AbstractIterator
        public Archive adapt(Archive.Entry entry) {
            try {
                return JarFileArchive.this.getNestedArchive(entry);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/JarFileArchive$JarFileEntry.class */
    private static class JarFileEntry implements Archive.Entry {
        private final JarEntry jarEntry;

        JarFileEntry(JarEntry jarEntry) {
            this.jarEntry = jarEntry;
        }

        JarEntry getJarEntry() {
            return this.jarEntry;
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public boolean isDirectory() {
            return this.jarEntry.isDirectory();
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public String getName() {
            return this.jarEntry.getName();
        }
    }
}
