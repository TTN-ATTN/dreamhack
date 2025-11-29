package org.springframework.boot.autoconfigure.gson;

import com.google.gson.GsonBuilder;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/gson/GsonBuilderCustomizer.class */
public interface GsonBuilderCustomizer {
    void customize(GsonBuilder gsonBuilder);
}
