package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.springframework.boot.loader.data.RandomAccessData;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFileEntries.class */
class JarFileEntries implements CentralDirectoryVisitor, Iterable<JarEntry> {
    private static final String META_INF_PREFIX = "META-INF/";
    private static final int BASE_VERSION = 8;
    private static final int RUNTIME_VERSION;
    private static final long LOCAL_FILE_HEADER_SIZE = 30;
    private static final char SLASH = '/';
    private static final char NO_SUFFIX = 0;
    protected static final int ENTRY_CACHE_SIZE = 25;
    private final JarFile jarFile;
    private final JarEntryFilter filter;
    private RandomAccessData centralDirectoryData;
    private int size;
    private int[] hashCodes;
    private Offsets centralDirectoryOffsets;
    private int[] positions;
    private Boolean multiReleaseJar;
    private JarEntryCertification[] certifications;
    private final Map<Integer, FileHeader> entriesCache = Collections.synchronizedMap(new LinkedHashMap<Integer, FileHeader>(16, 0.75f, true) { // from class: org.springframework.boot.loader.jar.JarFileEntries.1
        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Integer, FileHeader> eldest) {
            return size() >= 25;
        }
    });
    private static final Runnable NO_VALIDATION = () -> {
    };
    private static final Attributes.Name MULTI_RELEASE = new Attributes.Name("Multi-Release");

    static {
        int version;
        try {
            Object runtimeVersion = Runtime.class.getMethod("version", new Class[0]).invoke(null, new Object[0]);
            version = ((Integer) runtimeVersion.getClass().getMethod("major", new Class[0]).invoke(runtimeVersion, new Object[0])).intValue();
        } catch (Throwable th) {
            version = 8;
        }
        RUNTIME_VERSION = version;
    }

    JarFileEntries(JarFile jarFile, JarEntryFilter filter) {
        this.jarFile = jarFile;
        this.filter = filter;
        if (RUNTIME_VERSION == 8) {
            this.multiReleaseJar = false;
        }
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
        int maxSize = endRecord.getNumberOfRecords();
        this.centralDirectoryData = centralDirectoryData;
        this.hashCodes = new int[maxSize];
        this.centralDirectoryOffsets = Offsets.from(endRecord);
        this.positions = new int[maxSize];
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitFileHeader(CentralDirectoryFileHeader fileHeader, long dataOffset) {
        AsciiBytes name = applyFilter(fileHeader.getName());
        if (name != null) {
            add(name, dataOffset);
        }
    }

    private void add(AsciiBytes name, long dataOffset) {
        this.hashCodes[this.size] = name.hashCode();
        this.centralDirectoryOffsets.set(this.size, dataOffset);
        this.positions[this.size] = this.size;
        this.size++;
    }

    @Override // org.springframework.boot.loader.jar.CentralDirectoryVisitor
    public void visitEnd() {
        sort(0, this.size - 1);
        int[] positions = this.positions;
        this.positions = new int[positions.length];
        for (int i = 0; i < this.size; i++) {
            this.positions[positions[i]] = i;
        }
    }

    int getSize() {
        return this.size;
    }

    private void sort(int left, int right) {
        if (left < right) {
            int pivot = this.hashCodes[left + ((right - left) / 2)];
            int i = left;
            int j = right;
            while (i <= j) {
                while (this.hashCodes[i] < pivot) {
                    i++;
                }
                while (this.hashCodes[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    swap(i, j);
                    i++;
                    j--;
                }
            }
            if (left < j) {
                sort(left, j);
            }
            if (right > i) {
                sort(i, right);
            }
        }
    }

    private void swap(int i, int j) {
        swap(this.hashCodes, i, j);
        this.centralDirectoryOffsets.swap(i, j);
        swap(this.positions, i, j);
    }

    @Override // java.lang.Iterable
    public Iterator<JarEntry> iterator() {
        return new EntryIterator(NO_VALIDATION);
    }

    Iterator<JarEntry> iterator(Runnable validator) {
        return new EntryIterator(validator);
    }

    boolean containsEntry(CharSequence name) {
        return getEntry(name, FileHeader.class, true) != null;
    }

    JarEntry getEntry(CharSequence name) {
        return (JarEntry) getEntry(name, JarEntry.class, true);
    }

    InputStream getInputStream(String name) throws IOException {
        FileHeader entry = getEntry(name, FileHeader.class, false);
        return getInputStream(entry);
    }

    InputStream getInputStream(FileHeader entry) throws IOException {
        if (entry == null) {
            return null;
        }
        InputStream inputStream = getEntryData(entry).getInputStream();
        if (entry.getMethod() == 8) {
            inputStream = new ZipInflaterInputStream(inputStream, (int) entry.getSize());
        }
        return inputStream;
    }

    RandomAccessData getEntryData(String name) throws IOException {
        FileHeader entry = getEntry(name, FileHeader.class, false);
        if (entry == null) {
            return null;
        }
        return getEntryData(entry);
    }

    private RandomAccessData getEntryData(FileHeader entry) throws IOException {
        RandomAccessData data = this.jarFile.getData();
        byte[] localHeader = data.read(entry.getLocalHeaderOffset(), LOCAL_FILE_HEADER_SIZE);
        long nameLength = Bytes.littleEndianValue(localHeader, 26, 2);
        long extraLength = Bytes.littleEndianValue(localHeader, 28, 2);
        return data.getSubsection(entry.getLocalHeaderOffset() + LOCAL_FILE_HEADER_SIZE + nameLength + extraLength, entry.getCompressedSize());
    }

    private <T extends FileHeader> T getEntry(CharSequence charSequence, Class<T> cls, boolean z) {
        JarEntry jarEntry = (T) doGetEntry(charSequence, cls, z, null);
        if (!isMetaInfEntry(charSequence) && isMultiReleaseJar()) {
            AsciiBytes asciiBytesName = jarEntry instanceof JarEntry ? jarEntry.getAsciiBytesName() : new AsciiBytes(charSequence.toString());
            for (int i = RUNTIME_VERSION; i > 8; i--) {
                T t = (T) doGetEntry("META-INF/versions/" + i + "/" + ((Object) charSequence), cls, z, asciiBytesName);
                if (t != null) {
                    return t;
                }
            }
        }
        return jarEntry;
    }

    private boolean isMetaInfEntry(CharSequence name) {
        return name.toString().startsWith(META_INF_PREFIX);
    }

    private boolean isMultiReleaseJar() {
        Boolean multiRelease;
        Boolean multiRelease2 = this.multiReleaseJar;
        if (multiRelease2 != null) {
            return multiRelease2.booleanValue();
        }
        try {
            Manifest manifest = this.jarFile.getManifest();
            if (manifest == null) {
                multiRelease = false;
            } else {
                Attributes attributes = manifest.getMainAttributes();
                multiRelease = Boolean.valueOf(attributes.containsKey(MULTI_RELEASE));
            }
        } catch (IOException e) {
            multiRelease = false;
        }
        this.multiReleaseJar = multiRelease;
        return multiRelease.booleanValue();
    }

    private <T extends FileHeader> T doGetEntry(CharSequence charSequence, Class<T> cls, boolean z, AsciiBytes asciiBytes) {
        int iHashCode = AsciiBytes.hashCode(charSequence);
        FileHeader entry = getEntry(iHashCode, charSequence, (char) 0, cls, z, asciiBytes);
        if (entry == null) {
            entry = getEntry(AsciiBytes.hashCode(iHashCode, '/'), charSequence, '/', cls, z, asciiBytes);
        }
        return (T) entry;
    }

    private <T extends FileHeader> T getEntry(int i, CharSequence charSequence, char c, Class<T> cls, boolean z, AsciiBytes asciiBytes) {
        for (int firstIndex = getFirstIndex(i); firstIndex >= 0 && firstIndex < this.size && this.hashCodes[firstIndex] == i; firstIndex++) {
            T t = (T) getEntry(firstIndex, cls, z, asciiBytes);
            if (t.hasName(charSequence, c)) {
                return t;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T extends FileHeader> T getEntry(int i, Class<T> cls, boolean z, AsciiBytes asciiBytes) {
        try {
            long j = this.centralDirectoryOffsets.get(i);
            FileHeader fileHeader = this.entriesCache.get(Integer.valueOf(i));
            FileHeader fileHeaderFromRandomAccessData = fileHeader != null ? fileHeader : CentralDirectoryFileHeader.fromRandomAccessData(this.centralDirectoryData, j, this.filter);
            if (CentralDirectoryFileHeader.class.equals(fileHeaderFromRandomAccessData.getClass()) && cls.equals(JarEntry.class)) {
                fileHeaderFromRandomAccessData = new JarEntry(this.jarFile, i, (CentralDirectoryFileHeader) fileHeaderFromRandomAccessData, asciiBytes);
            }
            if (z && fileHeader != fileHeaderFromRandomAccessData) {
                this.entriesCache.put(Integer.valueOf(i), fileHeaderFromRandomAccessData);
            }
            return (T) fileHeaderFromRandomAccessData;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private int getFirstIndex(int hashCode) {
        int index = Arrays.binarySearch(this.hashCodes, 0, this.size, hashCode);
        if (index < 0) {
            return -1;
        }
        while (index > 0 && this.hashCodes[index - 1] == hashCode) {
            index--;
        }
        return index;
    }

    void clearCache() {
        this.entriesCache.clear();
    }

    private AsciiBytes applyFilter(AsciiBytes name) {
        return this.filter != null ? this.filter.apply(name) : name;
    }

    JarEntryCertification getCertification(JarEntry entry) throws IOException {
        JarEntryCertification[] certifications = this.certifications;
        if (certifications == null) {
            certifications = new JarEntryCertification[this.size];
            JarInputStream certifiedJarStream = new JarInputStream(this.jarFile.getData().getInputStream());
            Throwable th = null;
            try {
                while (true) {
                    java.util.jar.JarEntry certifiedEntry = certifiedJarStream.getNextJarEntry();
                    if (certifiedEntry == null) {
                        break;
                    }
                    certifiedJarStream.closeEntry();
                    int index = getEntryIndex(certifiedEntry.getName());
                    if (index != -1) {
                        certifications[index] = JarEntryCertification.from(certifiedEntry);
                    }
                }
                this.certifications = certifications;
            } finally {
                if (certifiedJarStream != null) {
                    if (0 != 0) {
                        try {
                            certifiedJarStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        certifiedJarStream.close();
                    }
                }
            }
        }
        JarEntryCertification certification = certifications[entry.getIndex()];
        return certification != null ? certification : JarEntryCertification.NONE;
    }

    private int getEntryIndex(CharSequence name) {
        int hashCode = AsciiBytes.hashCode(name);
        for (int index = getFirstIndex(hashCode); index >= 0 && index < this.size && this.hashCodes[index] == hashCode; index++) {
            FileHeader candidate = getEntry(index, FileHeader.class, false, null);
            if (candidate.hasName(name, (char) 0)) {
                return index;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void swap(long[] array, int i, int j) {
        long temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFileEntries$EntryIterator.class */
    private final class EntryIterator implements Iterator<JarEntry> {
        private final Runnable validator;
        private int index;

        private EntryIterator(Runnable validator) {
            this.index = 0;
            this.validator = validator;
            validator.run();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            this.validator.run();
            return this.index < JarFileEntries.this.size;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public JarEntry next() {
            this.validator.run();
            if (hasNext()) {
                int entryIndex = JarFileEntries.this.positions[this.index];
                this.index++;
                return (JarEntry) JarFileEntries.this.getEntry(entryIndex, JarEntry.class, false, null);
            }
            throw new NoSuchElementException();
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFileEntries$Offsets.class */
    private interface Offsets {
        void set(int index, long value);

        long get(int index);

        void swap(int i, int j);

        static Offsets from(CentralDirectoryEndRecord endRecord) {
            int size = endRecord.getNumberOfRecords();
            return endRecord.isZip64() ? new Zip64Offsets(size) : new ZipOffsets(size);
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFileEntries$ZipOffsets.class */
    private static final class ZipOffsets implements Offsets {
        private final int[] offsets;

        private ZipOffsets(int size) {
            this.offsets = new int[size];
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public void swap(int i, int j) {
            JarFileEntries.swap(this.offsets, i, j);
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public void set(int index, long value) {
            this.offsets[index] = (int) value;
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public long get(int index) {
            return this.offsets[index];
        }
    }

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarFileEntries$Zip64Offsets.class */
    private static final class Zip64Offsets implements Offsets {
        private final long[] offsets;

        private Zip64Offsets(int size) {
            this.offsets = new long[size];
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public void swap(int i, int j) {
            JarFileEntries.swap(this.offsets, i, j);
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public void set(int index, long value) {
            this.offsets[index] = value;
        }

        @Override // org.springframework.boot.loader.jar.JarFileEntries.Offsets
        public long get(int index) {
            return this.offsets[index];
        }
    }
}
