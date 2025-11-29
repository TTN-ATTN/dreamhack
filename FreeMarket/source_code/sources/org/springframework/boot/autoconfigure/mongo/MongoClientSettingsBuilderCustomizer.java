package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoClientSettingsBuilderCustomizer.class */
public interface MongoClientSettingsBuilderCustomizer {
    void customize(MongoClientSettings.Builder clientSettingsBuilder);
}
