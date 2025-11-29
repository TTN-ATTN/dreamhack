package org.springframework.boot.autoconfigure.web.servlet;

import io.undertow.Undertow;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration.class */
class ServletWebServerFactoryConfiguration {
    ServletWebServerFactoryConfiguration() {
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedTomcat.class */
    static class EmbeddedTomcat {
        EmbeddedTomcat() {
        }

        @Bean
        TomcatServletWebServerFactory tomcatServletWebServerFactory(ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers, ObjectProvider<TomcatContextCustomizer> contextCustomizers, ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
            TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
            factory.getTomcatConnectorCustomizers().addAll((Collection) connectorCustomizers.orderedStream().collect(Collectors.toList()));
            factory.getTomcatContextCustomizers().addAll((Collection) contextCustomizers.orderedStream().collect(Collectors.toList()));
            factory.getTomcatProtocolHandlerCustomizers().addAll((Collection) protocolHandlerCustomizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedJetty.class */
    static class EmbeddedJetty {
        EmbeddedJetty() {
        }

        @Bean
        JettyServletWebServerFactory jettyServletWebServerFactory(ObjectProvider<JettyServerCustomizer> serverCustomizers) {
            JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
            factory.getServerCustomizers().addAll((Collection) serverCustomizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedUndertow.class */
    static class EmbeddedUndertow {
        EmbeddedUndertow() {
        }

        @Bean
        UndertowServletWebServerFactory undertowServletWebServerFactory(ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers, ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
            UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
            factory.getDeploymentInfoCustomizers().addAll((Collection) deploymentInfoCustomizers.orderedStream().collect(Collectors.toList()));
            factory.getBuilderCustomizers().addAll((Collection) builderCustomizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }

        @Bean
        UndertowServletWebServerFactoryCustomizer undertowServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
            return new UndertowServletWebServerFactoryCustomizer(serverProperties);
        }
    }
}
