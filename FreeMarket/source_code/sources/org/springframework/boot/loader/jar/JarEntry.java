package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarEntry.class */
class JarEntry extends java.util.jar.JarEntry implements FileHeader {
    private final int index;
    private final AsciiBytes name;
    private final AsciiBytes headerName;
    private final JarFile jarFile;
    private long localHeaderOffset;
    private volatile JarEntryCertification certification;

    JarEntry(JarFile jarFile, int index, CentralDirectoryFileHeader header, AsciiBytes nameAlias) {
        super(nameAlias != null ? nameAlias.toString() : header.getName().toString());
        this.index = index;
        this.name = nameAlias != null ? nameAlias : header.getName();
        this.headerName = header.getName();
        this.jarFile = jarFile;
        this.localHeaderOffset = header.getLocalHeaderOffset();
        setCompressedSize(header.getCompressedSize());
        setMethod(header.getMethod());
        setCrc(header.getCrc());
        setComment(header.getComment().toString());
        setSize(header.getSize());
        setTime(header.getTime());
        if (header.hasExtra()) {
            setExtra(header.getExtra());
        }
    }

    int getIndex() {
        return this.index;
    }

    AsciiBytes getAsciiBytesName() {
        return this.name;
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public boolean hasName(CharSequence name, char suffix) {
        return this.headerName.matches(name, suffix);
    }

    URL getUrl() throws MalformedURLException {
        return new URL(this.jarFile.getUrl(), getName());
    }

    @Override // java.util.jar.JarEntry
    public Attributes getAttributes() throws IOException {
        Manifest manifest = this.jarFile.getManifest();
        if (manifest != null) {
            return manifest.getAttributes(getName());
        }
        return null;
    }

    @Override // java.util.jar.JarEntry
    public Certificate[] getCertificates() {
        return getCertification().getCertificates();
    }

    @Override // java.util.jar.JarEntry
    public CodeSigner[] getCodeSigners() {
        return getCertification().getCodeSigners();
    }

    private JarEntryCertification getCertification() {
        if (!this.jarFile.isSigned()) {
            return JarEntryCertification.NONE;
        }
        JarEntryCertification certification = this.certification;
        if (certification == null) {
            certification = this.jarFile.getCertification(this);
            this.certification = certification;
        }
        return certification;
    }

    @Override // org.springframework.boot.loader.jar.FileHeader
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }
}
