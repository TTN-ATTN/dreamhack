package org.springframework.boot.web.embedded.jetty;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.springframework.boot.web.server.Compression;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/JettyHandlerWrappers.class */
final class JettyHandlerWrappers {
    private JettyHandlerWrappers() {
    }

    static HandlerWrapper createGzipHandlerWrapper(Compression compression) {
        GzipHandler handler = new GzipHandler();
        handler.setMinGzipSize((int) compression.getMinResponseSize().toBytes());
        handler.setIncludedMimeTypes(compression.getMimeTypes());
        for (HttpMethod httpMethod : HttpMethod.values()) {
            handler.addIncludedMethods(new String[]{httpMethod.name()});
        }
        if (compression.getExcludedUserAgents() != null) {
            try {
                handler.setExcludedAgentPatterns(compression.getExcludedUserAgents());
            } catch (NoSuchMethodError e) {
            }
        }
        return handler;
    }

    static HandlerWrapper createServerHeaderHandlerWrapper(String header) {
        return new ServerHeaderHandler(header);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/jetty/JettyHandlerWrappers$ServerHeaderHandler.class */
    private static class ServerHeaderHandler extends HandlerWrapper {
        private static final String SERVER_HEADER = "server";
        private final String value;

        ServerHeaderHandler(String value) {
            this.value = value;
        }

        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            if (!response.getHeaderNames().contains(SERVER_HEADER)) {
                response.setHeader(SERVER_HEADER, this.value);
            }
            super.handle(target, baseRequest, request, response);
        }
    }
}
