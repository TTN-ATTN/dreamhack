package org.springframework.boot.web.embedded.undertow;

import java.io.File;
import java.util.Collection;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/undertow/ConfigurableUndertowWebServerFactory.class */
public interface ConfigurableUndertowWebServerFactory extends ConfigurableWebServerFactory {
    void setBuilderCustomizers(Collection<? extends UndertowBuilderCustomizer> customizers);

    void addBuilderCustomizers(UndertowBuilderCustomizer... customizers);

    void setBufferSize(Integer bufferSize);

    void setIoThreads(Integer ioThreads);

    void setWorkerThreads(Integer workerThreads);

    void setUseDirectBuffers(Boolean useDirectBuffers);

    void setAccessLogDirectory(File accessLogDirectory);

    void setAccessLogPattern(String accessLogPattern);

    void setAccessLogPrefix(String accessLogPrefix);

    void setAccessLogSuffix(String accessLogSuffix);

    void setAccessLogEnabled(boolean accessLogEnabled);

    void setAccessLogRotate(boolean accessLogRotate);

    void setUseForwardHeaders(boolean useForwardHeaders);
}
