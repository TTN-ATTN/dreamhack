package org.springframework.boot.autoconfigure.websocket.servlet;

import io.undertow.websockets.jsr.Bootstrap;
import javax.servlet.Servlet;
import javax.websocket.server.ServerContainer;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(before = {ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnClass({Servlet.class, ServerContainer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration.class */
public class WebSocketServletAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class, WsSci.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$TomcatWebSocketConfiguration.class */
    static class TomcatWebSocketConfiguration {
        TomcatWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        TomcatWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new TomcatWebSocketServletWebServerCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({WebSocketServerContainerInitializer.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$JettyWebSocketConfiguration.class */
    static class JettyWebSocketConfiguration {
        JettyWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        JettyWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new JettyWebSocketServletWebServerCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = {"org.eclipse.jetty.websocket.javax.server.internal.JavaxWebSocketServerContainer", "org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer"})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$Jetty10WebSocketConfiguration.class */
    static class Jetty10WebSocketConfiguration {
        Jetty10WebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        Jetty10WebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new Jetty10WebSocketServletWebServerCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Bootstrap.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketServletAutoConfiguration$UndertowWebSocketConfiguration.class */
    static class UndertowWebSocketConfiguration {
        UndertowWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketServletWebServerCustomizer"})
        @Bean
        UndertowWebSocketServletWebServerCustomizer websocketServletWebServerCustomizer() {
            return new UndertowWebSocketServletWebServerCustomizer();
        }
    }
}
