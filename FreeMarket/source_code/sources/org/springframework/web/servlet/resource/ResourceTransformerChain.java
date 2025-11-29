package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/resource/ResourceTransformerChain.class */
public interface ResourceTransformerChain {
    ResourceResolverChain getResolverChain();

    Resource transform(HttpServletRequest request, Resource resource) throws IOException;
}
