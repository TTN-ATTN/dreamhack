package org.springframework.boot.web.embedded.jetty;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedErrorHandler.class */
class JettyEmbeddedErrorHandler extends ErrorPageErrorHandler {
    private static final Set<String> HANDLED_HTTP_METHODS = new HashSet(Arrays.asList("GET", WebContentGenerator.METHOD_POST, WebContentGenerator.METHOD_HEAD));

    JettyEmbeddedErrorHandler() {
    }

    public boolean errorPageForMethod(String method) {
        return true;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!HANDLED_HTTP_METHODS.contains(baseRequest.getMethod())) {
            baseRequest.setMethod("GET");
        }
        super.handle(target, baseRequest, request, response);
    }
}
