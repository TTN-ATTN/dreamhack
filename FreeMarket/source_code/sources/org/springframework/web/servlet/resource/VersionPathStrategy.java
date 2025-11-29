package org.springframework.web.servlet.resource;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/VersionPathStrategy.class */
public interface VersionPathStrategy {
    @Nullable
    String extractVersion(String requestPath);

    String removeVersion(String requestPath, String version);

    String addVersion(String requestPath, String version);
}
