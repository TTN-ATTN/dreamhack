package org.springframework.http.server.reactive;

import java.security.cert.X509Certificate;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/SslInfo.class */
public interface SslInfo {
    @Nullable
    String getSessionId();

    @Nullable
    X509Certificate[] getPeerCertificates();
}
