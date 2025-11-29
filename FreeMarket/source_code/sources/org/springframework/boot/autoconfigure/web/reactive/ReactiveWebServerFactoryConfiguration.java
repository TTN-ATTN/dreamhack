package org.springframework.boot.autoconfigure.web.reactive;

import io.undertow.Undertow;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyRouteProvider;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.http.server.HttpServer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration.class */
abstract class ReactiveWebServerFactoryConfiguration {
    ReactiveWebServerFactoryConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpServer.class})
    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedNetty.class */
    static class EmbeddedNetty {
        EmbeddedNetty() {
        }

        @Bean
        NettyReactiveWebServerFactory nettyReactiveWebServerFactory(ReactorResourceFactory resourceFactory, ObjectProvider<NettyRouteProvider> routes, ObjectProvider<NettyServerCustomizer> serverCustomizers) {
            NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
            serverFactory.setResourceFactory(resourceFactory);
            Stream<NettyRouteProvider> streamOrderedStream = routes.orderedStream();
            serverFactory.getClass();
            streamOrderedStream.forEach(xva$0 -> {
                serverFactory.addRouteProviders(xva$0);
            });
            serverFactory.getServerCustomizers().addAll((Collection) serverCustomizers.orderedStream().collect(Collectors.toList()));
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedTomcat.class */
    static class EmbeddedTomcat {
        EmbeddedTomcat() {
        }

        @Bean
        TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory(ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers, ObjectProvider<TomcatContextCustomizer> contextCustomizers, ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
            TomcatReactiveWebServerFactory factory = new TomcatReactiveWebServerFactory();
            factory.getTomcatConnectorCustomizers().addAll((Collection) connectorCustomizers.orderedStream().collect(Collectors.toList()));
            factory.getTomcatContextCustomizers().addAll((Collection) contextCustomizers.orderedStream().collect(Collectors.toList()));
            factory.getTomcatProtocolHandlerCustomizers().addAll((Collection) protocolHandlerCustomizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Server.class, ServletHolder.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedJetty.class */
    static class EmbeddedJetty {
        EmbeddedJetty() {
        }

        @ConditionalOnMissingBean
        @Bean
        JettyResourceFactory jettyServerResourceFactory() {
            return new JettyResourceFactory();
        }

        @Bean
        JettyReactiveWebServerFactory jettyReactiveWebServerFactory(JettyResourceFactory resourceFactory, ObjectProvider<JettyServerCustomizer> serverCustomizers) {
            JettyReactiveWebServerFactory serverFactory = new JettyReactiveWebServerFactory();
            serverFactory.getServerCustomizers().addAll((Collection) serverCustomizers.orderedStream().collect(Collectors.toList()));
            serverFactory.setResourceFactory(resourceFactory);
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Undertow.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedUndertow.class */
    static class EmbeddedUndertow {
        EmbeddedUndertow() {
        }

        @Bean
        UndertowReactiveWebServerFactory undertowReactiveWebServerFactory(ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
            UndertowReactiveWebServerFactory factory = new UndertowReactiveWebServerFactory();
            factory.getBuilderCustomizers().addAll((Collection) builderCustomizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }
    }
}
