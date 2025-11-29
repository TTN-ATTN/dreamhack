package org.springframework.boot.web.server;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/CertificateFileSslStoreProvider.class */
public final class CertificateFileSslStoreProvider implements SslStoreProvider {
    private static final String KEY_PASSWORD = "";
    private static final String DEFAULT_KEY_ALIAS = "spring-boot-web";
    private final Ssl ssl;

    private CertificateFileSslStoreProvider(Ssl ssl) {
        this.ssl = ssl;
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public KeyStore getKeyStore() throws Exception {
        return createKeyStore(this.ssl.getCertificate(), this.ssl.getCertificatePrivateKey(), this.ssl.getKeyStoreType(), this.ssl.getKeyAlias());
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public KeyStore getTrustStore() throws Exception {
        if (this.ssl.getTrustCertificate() == null) {
            return null;
        }
        return createKeyStore(this.ssl.getTrustCertificate(), this.ssl.getTrustCertificatePrivateKey(), this.ssl.getTrustStoreType(), this.ssl.getKeyAlias());
    }

    @Override // org.springframework.boot.web.server.SslStoreProvider
    public String getKeyPassword() {
        return "";
    }

    private KeyStore createKeyStore(String certPath, String keyPath, String storeType, String keyAlias) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException {
        String defaultType;
        if (storeType != null) {
            defaultType = storeType;
        } else {
            try {
                defaultType = KeyStore.getDefaultType();
            } catch (IOException | GeneralSecurityException ex) {
                throw new IllegalStateException("Error creating KeyStore: " + ex.getMessage(), ex);
            }
        }
        KeyStore keyStore = KeyStore.getInstance(defaultType);
        keyStore.load(null);
        X509Certificate[] certificates = CertificateParser.parse(certPath);
        PrivateKey privateKey = keyPath != null ? PrivateKeyParser.parse(keyPath) : null;
        try {
            addCertificates(keyStore, certificates, privateKey, keyAlias);
            return keyStore;
        } catch (KeyStoreException ex2) {
            throw new IllegalStateException("Error adding certificates to KeyStore: " + ex2.getMessage(), ex2);
        }
    }

    private void addCertificates(KeyStore keyStore, X509Certificate[] certificates, PrivateKey privateKey, String keyAlias) throws KeyStoreException {
        String alias = keyAlias != null ? keyAlias : DEFAULT_KEY_ALIAS;
        if (privateKey != null) {
            keyStore.setKeyEntry(alias, privateKey, "".toCharArray(), certificates);
            return;
        }
        for (int index = 0; index < certificates.length; index++) {
            keyStore.setCertificateEntry(alias + "-" + index, certificates[index]);
        }
    }

    public static SslStoreProvider from(Ssl ssl) {
        if (ssl != null && ssl.isEnabled() && ssl.getCertificate() != null && ssl.getCertificatePrivateKey() != null) {
            return new CertificateFileSslStoreProvider(ssl);
        }
        return null;
    }
}
