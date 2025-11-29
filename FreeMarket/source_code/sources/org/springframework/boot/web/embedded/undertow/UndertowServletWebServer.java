package org.springframework.boot.web.embedded.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServer.class */
public class UndertowServletWebServer extends UndertowWebServer {
    private final String contextPath;
    private final DeploymentManager manager;

    public UndertowServletWebServer(Undertow.Builder builder, Iterable<HttpHandlerFactory> httpHandlerFactories, String contextPath, boolean autoStart) {
        super(builder, httpHandlerFactories, autoStart);
        this.contextPath = contextPath;
        this.manager = findManager(httpHandlerFactories);
    }

    private DeploymentManager findManager(Iterable<HttpHandlerFactory> httpHandlerFactories) {
        for (HttpHandlerFactory httpHandlerFactory : httpHandlerFactories) {
            if (httpHandlerFactory instanceof DeploymentManagerHttpHandlerFactory) {
                return ((DeploymentManagerHttpHandlerFactory) httpHandlerFactory).getDeploymentManager();
            }
        }
        return null;
    }

    @Override // org.springframework.boot.web.embedded.undertow.UndertowWebServer
    protected HttpHandler createHttpHandler() {
        HttpHandler handler = super.createHttpHandler();
        if (StringUtils.hasLength(this.contextPath)) {
            handler = Handlers.path().addPrefixPath(this.contextPath, handler);
        }
        return handler;
    }

    @Override // org.springframework.boot.web.embedded.undertow.UndertowWebServer
    protected String getStartLogMessage() {
        String message = super.getStartLogMessage();
        if (StringUtils.hasText(this.contextPath)) {
            message = message + " with context path '" + this.contextPath + "'";
        }
        return message;
    }

    public DeploymentManager getDeploymentManager() {
        return this.manager;
    }
}
