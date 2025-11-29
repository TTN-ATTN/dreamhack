package org.springframework.boot.web.server;

import java.security.KeyStore;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/server/SslStoreProvider.class */
public interface SslStoreProvider {
    KeyStore getKeyStore() throws Exception;

    KeyStore getTrustStore() throws Exception;

    default String getKeyPassword() {
        return null;
    }
}
