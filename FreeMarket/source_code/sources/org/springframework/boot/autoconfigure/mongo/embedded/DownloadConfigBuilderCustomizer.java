package org.springframework.boot.autoconfigure.mongo.embedded;

import de.flapdoodle.embed.process.config.store.ImmutableDownloadConfig;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/embedded/DownloadConfigBuilderCustomizer.class */
public interface DownloadConfigBuilderCustomizer {
    void customize(ImmutableDownloadConfig.Builder downloadConfigBuilder);
}
