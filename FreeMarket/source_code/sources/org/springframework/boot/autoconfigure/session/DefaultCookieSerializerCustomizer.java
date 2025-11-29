package org.springframework.boot.autoconfigure.session;

import org.springframework.session.web.http.DefaultCookieSerializer;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/DefaultCookieSerializerCustomizer.class */
public interface DefaultCookieSerializerCustomizer {
    void customize(DefaultCookieSerializer cookieSerializer);
}
