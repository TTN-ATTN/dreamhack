package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceResolverChain.class */
public interface ResourceResolverChain {
    @Nullable
    Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations);

    @Nullable
    String resolveUrlPath(String resourcePath, List<? extends Resource> locations);
}
