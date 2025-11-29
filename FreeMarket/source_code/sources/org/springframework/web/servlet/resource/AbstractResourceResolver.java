package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/AbstractResourceResolver.class */
public abstract class AbstractResourceResolver implements ResourceResolver {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    protected abstract Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain);

    @Nullable
    protected abstract String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain);

    @Override // org.springframework.web.servlet.resource.ResourceResolver
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return resolveResourceInternal(request, requestPath, locations, chain);
    }

    @Override // org.springframework.web.servlet.resource.ResourceResolver
    @Nullable
    public String resolveUrlPath(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return resolveUrlPathInternal(resourceUrlPath, locations, chain);
    }
}
