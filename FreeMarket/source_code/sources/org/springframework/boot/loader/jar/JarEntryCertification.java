package org.springframework.boot.loader.jar;

import java.security.CodeSigner;
import java.security.cert.Certificate;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarEntryCertification.class */
class JarEntryCertification {
    static final JarEntryCertification NONE = new JarEntryCertification(null, null);
    private final Certificate[] certificates;
    private final CodeSigner[] codeSigners;

    JarEntryCertification(Certificate[] certificates, CodeSigner[] codeSigners) {
        this.certificates = certificates;
        this.codeSigners = codeSigners;
    }

    Certificate[] getCertificates() {
        if (this.certificates != null) {
            return (Certificate[]) this.certificates.clone();
        }
        return null;
    }

    CodeSigner[] getCodeSigners() {
        if (this.codeSigners != null) {
            return (CodeSigner[]) this.codeSigners.clone();
        }
        return null;
    }

    static JarEntryCertification from(java.util.jar.JarEntry certifiedEntry) {
        Certificate[] certificates = certifiedEntry != null ? certifiedEntry.getCertificates() : null;
        CodeSigner[] codeSigners = certifiedEntry != null ? certifiedEntry.getCodeSigners() : null;
        if (certificates == null && codeSigners == null) {
            return NONE;
        }
        return new JarEntryCertification(certificates, codeSigners);
    }
}
