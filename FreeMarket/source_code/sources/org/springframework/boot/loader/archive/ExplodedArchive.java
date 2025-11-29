package org.springframework.boot.loader.archive;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive.class */
public class ExplodedArchive implements Archive {
    private static final Set<String> SKIPPED_NAMES = new HashSet(Arrays.asList(".", CallerDataConverter.DEFAULT_RANGE_DELIMITER));
    private final File root;
    private final boolean recursive;
    private File manifestFile;
    private Manifest manifest;

    public ExplodedArchive(File root) {
        this(root, true);
    }

    public ExplodedArchive(File root, boolean recursive) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("Invalid source directory " + root);
        }
        this.root = root;
        this.recursive = recursive;
        this.manifestFile = getManifestFile(root);
    }

    private File getManifestFile(File root) {
        File metaInf = new File(root, "META-INF");
        return new File(metaInf, "MANIFEST.MF");
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public URL getUrl() throws MalformedURLException {
        return this.root.toURI().toURL();
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Manifest getManifest() throws IOException {
        if (this.manifest == null && this.manifestFile.exists()) {
            FileInputStream inputStream = new FileInputStream(this.manifestFile);
            Throwable th = null;
            try {
                try {
                    this.manifest = new Manifest(inputStream);
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
                } finally {
                }
            } catch (Throwable th3) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th3;
            }
        }
        return this.manifest;
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public Iterator<Archive> getNestedArchives(Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) throws IOException {
        return new ArchiveIterator(this.root, this.recursive, searchFilter, includeFilter);
    }

    @Override // org.springframework.boot.loader.archive.Archive, java.lang.Iterable
    @Deprecated
    public Iterator<Archive.Entry> iterator() {
        return new EntryIterator(this.root, this.recursive, null, null);
    }

    protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
        File file = ((FileEntry) entry).getFile();
        return file.isDirectory() ? new ExplodedArchive(file) : new SimpleJarFileArchive((FileEntry) entry);
    }

    @Override // org.springframework.boot.loader.archive.Archive
    public boolean isExploded() {
        return true;
    }

    public String toString() {
        try {
            return getUrl().toString();
        } catch (Exception e) {
            return "exploded archive";
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive$AbstractIterator.class */
    private static abstract class AbstractIterator<T> implements Iterator<T> {
        private static final Comparator<File> entryComparator = Comparator.comparing((v0) -> {
            return v0.getAbsolutePath();
        });
        private final File root;
        private final boolean recursive;
        private final Archive.EntryFilter searchFilter;
        private final Archive.EntryFilter includeFilter;
        private final Deque<Iterator<File>> stack = new LinkedList();
        private FileEntry current;
        private String rootUrl;

        protected abstract T adapt(FileEntry entry);

        AbstractIterator(File root, boolean recursive, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            this.root = root;
            this.rootUrl = this.root.toURI().getPath();
            this.recursive = recursive;
            this.searchFilter = searchFilter;
            this.includeFilter = includeFilter;
            this.stack.add(listFiles(root));
            this.current = poll();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        @Override // java.util.Iterator
        public T next() {
            FileEntry entry = this.current;
            if (entry == null) {
                throw new NoSuchElementException();
            }
            this.current = poll();
            return adapt(entry);
        }

        private FileEntry poll() {
            while (!this.stack.isEmpty()) {
                while (this.stack.peek().hasNext()) {
                    File file = this.stack.peek().next();
                    if (!ExplodedArchive.SKIPPED_NAMES.contains(file.getName())) {
                        FileEntry entry = getFileEntry(file);
                        if (isListable(entry)) {
                            this.stack.addFirst(listFiles(file));
                        }
                        if (this.includeFilter == null || this.includeFilter.matches(entry)) {
                            return entry;
                        }
                    }
                }
                this.stack.poll();
            }
            return null;
        }

        private FileEntry getFileEntry(File file) {
            URI uri = file.toURI();
            String name = uri.getPath().substring(this.rootUrl.length());
            try {
                return new FileEntry(name, file, uri.toURL());
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }

        private boolean isListable(FileEntry entry) {
            return entry.isDirectory() && (this.recursive || entry.getFile().getParentFile().equals(this.root)) && ((this.searchFilter == null || this.searchFilter.matches(entry)) && (this.includeFilter == null || !this.includeFilter.matches(entry)));
        }

        private Iterator<File> listFiles(File file) {
            File[] files = file.listFiles();
            if (files == null) {
                return Collections.emptyIterator();
            }
            Arrays.sort(files, entryComparator);
            return Arrays.asList(files).iterator();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive$EntryIterator.class */
    private static class EntryIterator extends AbstractIterator<Archive.Entry> {
        EntryIterator(File root, boolean recursive, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            super(root, recursive, searchFilter, includeFilter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.boot.loader.archive.ExplodedArchive.AbstractIterator
        public Archive.Entry adapt(FileEntry entry) {
            return entry;
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive$ArchiveIterator.class */
    private static class ArchiveIterator extends AbstractIterator<Archive> {
        ArchiveIterator(File root, boolean recursive, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
            super(root, recursive, searchFilter, includeFilter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.boot.loader.archive.ExplodedArchive.AbstractIterator
        public Archive adapt(FileEntry entry) {
            File file = entry.getFile();
            return file.isDirectory() ? new ExplodedArchive(file) : new SimpleJarFileArchive(entry);
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive$FileEntry.class */
    private static class FileEntry implements Archive.Entry {
        private final String name;
        private final File file;
        private final URL url;

        FileEntry(String name, File file, URL url) {
            this.name = name;
            this.file = file;
            this.url = url;
        }

        File getFile() {
            return this.file;
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        @Override // org.springframework.boot.loader.archive.Archive.Entry
        public String getName() {
            return this.name;
        }

        URL getUrl() {
            return this.url;
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/ExplodedArchive$SimpleJarFileArchive.class */
    private static class SimpleJarFileArchive implements Archive {
        private final URL url;

        SimpleJarFileArchive(FileEntry file) {
            this.url = file.getUrl();
        }

        @Override // org.springframework.boot.loader.archive.Archive
        public URL getUrl() throws MalformedURLException {
            return this.url;
        }

        @Override // org.springframework.boot.loader.archive.Archive
        public Manifest getManifest() throws IOException {
            return null;
        }

        @Override // org.springframework.boot.loader.archive.Archive
        public Iterator<Archive> getNestedArchives(Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) throws IOException {
            return Collections.emptyIterator();
        }

        @Override // org.springframework.boot.loader.archive.Archive, java.lang.Iterable
        @Deprecated
        public Iterator<Archive.Entry> iterator() {
            return Collections.emptyIterator();
        }

        public String toString() {
            try {
                return getUrl().toString();
            } catch (Exception e) {
                return "jar archive";
            }
        }
    }
}
