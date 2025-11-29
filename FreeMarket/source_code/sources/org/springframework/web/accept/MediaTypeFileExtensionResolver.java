package org.springframework.web.accept;

import java.util.List;
import org.springframework.http.MediaType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/accept/MediaTypeFileExtensionResolver.class */
public interface MediaTypeFileExtensionResolver {
    List<String> resolveFileExtensions(MediaType mediaType);

    List<String> getAllFileExtensions();
}
