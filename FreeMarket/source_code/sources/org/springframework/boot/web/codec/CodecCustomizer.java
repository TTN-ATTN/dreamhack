package org.springframework.boot.web.codec;

import org.springframework.http.codec.CodecConfigurer;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/codec/CodecCustomizer.class */
public interface CodecCustomizer {
    void customize(CodecConfigurer configurer);
}
