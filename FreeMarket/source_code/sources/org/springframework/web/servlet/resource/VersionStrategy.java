package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/VersionStrategy.class */
public interface VersionStrategy extends VersionPathStrategy {
    String getResourceVersion(Resource resource);
}
