package org.springframework.web.cors;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/cors/CorsProcessor.class */
public interface CorsProcessor {
    boolean processRequest(@Nullable CorsConfiguration configuration, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
