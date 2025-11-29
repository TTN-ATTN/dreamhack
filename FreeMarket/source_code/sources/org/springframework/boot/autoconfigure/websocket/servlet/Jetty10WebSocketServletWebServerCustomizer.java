package org.springframework.boot.autoconfigure.websocket.servlet;

import java.lang.reflect.Method;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/Jetty10WebSocketServletWebServerCustomizer.class */
class Jetty10WebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory>, Ordered {
    static final String JETTY_WEB_SOCKET_SERVER_CONTAINER = "org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer";
    static final String JAVAX_WEB_SOCKET_SERVER_CONTAINER = "org.eclipse.jetty.websocket.javax.server.internal.JavaxWebSocketServerContainer";

    Jetty10WebSocketServletWebServerCustomizer() {
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(JettyServletWebServerFactory factory) {
        factory.addConfigurations(new AbstractConfiguration() { // from class: org.springframework.boot.autoconfigure.websocket.servlet.Jetty10WebSocketServletWebServerCustomizer.1
            public void configure(WebAppContext context) throws Exception {
                ContextHandler.Context servletContext = context.getServletContext();
                Class<?> jettyContainer = ClassUtils.forName(Jetty10WebSocketServletWebServerCustomizer.JETTY_WEB_SOCKET_SERVER_CONTAINER, null);
                Method getJettyContainer = ReflectionUtils.findMethod(jettyContainer, "getContainer", ServletContext.class);
                Server server = context.getServer();
                if (ReflectionUtils.invokeMethod(getJettyContainer, null, servletContext) == null) {
                    ensureWebSocketComponents(server, servletContext);
                    ensureContainer(jettyContainer, servletContext);
                }
                Class<?> javaxContainer = ClassUtils.forName(Jetty10WebSocketServletWebServerCustomizer.JAVAX_WEB_SOCKET_SERVER_CONTAINER, null);
                Method getJavaxContainer = ReflectionUtils.findMethod(javaxContainer, "getContainer", ServletContext.class);
                if (ReflectionUtils.invokeMethod(getJavaxContainer, "getContainer", servletContext) == null) {
                    ensureWebSocketComponents(server, servletContext);
                    ensureUpgradeFilter(servletContext);
                    ensureMappings(servletContext);
                    ensureContainer(javaxContainer, servletContext);
                }
            }

            private void ensureWebSocketComponents(Server server, ServletContext servletContext) throws LinkageError, ClassNotFoundException {
                Class<?> webSocketServerComponents = ClassUtils.forName("org.eclipse.jetty.websocket.core.server.WebSocketServerComponents", null);
                Method ensureWebSocketComponents = ReflectionUtils.findMethod(webSocketServerComponents, "ensureWebSocketComponents", Server.class, ServletContext.class);
                ReflectionUtils.invokeMethod(ensureWebSocketComponents, null, server, servletContext);
            }

            private void ensureContainer(Class<?> container, ServletContext servletContext) {
                Method ensureContainer = ReflectionUtils.findMethod(container, "ensureContainer", ServletContext.class);
                ReflectionUtils.invokeMethod(ensureContainer, null, servletContext);
            }

            private void ensureUpgradeFilter(ServletContext servletContext) throws LinkageError, ClassNotFoundException {
                Class<?> webSocketUpgradeFilter = ClassUtils.forName("org.eclipse.jetty.websocket.servlet.WebSocketUpgradeFilter", null);
                Method ensureFilter = ReflectionUtils.findMethod(webSocketUpgradeFilter, "ensureFilter", ServletContext.class);
                ReflectionUtils.invokeMethod(ensureFilter, null, servletContext);
            }

            private void ensureMappings(ServletContext servletContext) throws LinkageError, ClassNotFoundException {
                Class<?> webSocketMappings = ClassUtils.forName("org.eclipse.jetty.websocket.core.server.WebSocketMappings", null);
                Method ensureMappings = ReflectionUtils.findMethod(webSocketMappings, "ensureMappings", ServletContext.class);
                ReflectionUtils.invokeMethod(ensureMappings, null, servletContext);
            }
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
