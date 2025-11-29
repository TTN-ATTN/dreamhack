package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.tomcat.websocket.Constants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.netty.http.server.HttpServer;

@EnableConfigurationProperties({RSocketProperties.class})
@AutoConfiguration(after = {RSocketStrategiesAutoConfiguration.class})
@ConditionalOnClass({RSocketServer.class, RSocketStrategies.class, HttpServer.class, TcpServerTransport.class})
@ConditionalOnBean({RSocketMessageHandler.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration.class */
public class RSocketServerAutoConfiguration {

    @Conditional({OnRSocketWebServerCondition.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$WebFluxServerConfiguration.class */
    static class WebFluxServerConfiguration {
        WebFluxServerConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketWebSocketNettyRouteProvider rSocketWebsocketRouteProvider(RSocketProperties properties, RSocketMessageHandler messageHandler, ObjectProvider<RSocketServerCustomizer> customizers) {
            return new RSocketWebSocketNettyRouteProvider(properties.getServer().getMappingPath(), messageHandler.responder(), customizers.orderedStream());
        }
    }

    @ConditionalOnClass({ReactorResourceFactory.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"port"})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$EmbeddedServerConfiguration.class */
    static class EmbeddedServerConfiguration {
        EmbeddedServerConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketServerFactory rSocketServerFactory(RSocketProperties properties, ReactorResourceFactory resourceFactory, ObjectProvider<RSocketServerCustomizer> customizers) {
            NettyRSocketServerFactory factory = new NettyRSocketServerFactory();
            factory.setResourceFactory(resourceFactory);
            factory.setTransport(properties.getServer().getTransport());
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source sourceFrom = map.from((PropertyMapper) properties.getServer().getAddress());
            factory.getClass();
            sourceFrom.to(factory::setAddress);
            PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) properties.getServer().getPort());
            factory.getClass();
            sourceFrom2.to((v1) -> {
                r1.setPort(v1);
            });
            PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) properties.getServer().getFragmentSize());
            factory.getClass();
            sourceFrom3.to(factory::setFragmentSize);
            PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) properties.getServer().getSsl());
            factory.getClass();
            sourceFrom4.to(factory::setSsl);
            factory.setRSocketServerCustomizers((Collection) customizers.orderedStream().collect(Collectors.toList()));
            return factory;
        }

        @ConditionalOnMissingBean
        @Bean
        RSocketServerBootstrap rSocketServerBootstrap(RSocketServerFactory rSocketServerFactory, RSocketMessageHandler rSocketMessageHandler) {
            return new RSocketServerBootstrap(rSocketServerFactory, rSocketMessageHandler.responder());
        }

        @Bean
        RSocketServerCustomizer frameDecoderRSocketServerCustomizer(RSocketMessageHandler rSocketMessageHandler) {
            return server -> {
                if (rSocketMessageHandler.getRSocketStrategies().dataBufferFactory() instanceof NettyDataBufferFactory) {
                    server.payloadDecoder(PayloadDecoder.ZERO_COPY);
                }
            };
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition.class */
    static class OnRSocketWebServerCondition extends AllNestedConditions {
        OnRSocketWebServerCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$IsReactiveWebApplication.class */
        static class IsReactiveWebApplication {
            IsReactiveWebApplication() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"port"}, matchIfMissing = true)
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasNoPortConfigured.class */
        static class HasNoPortConfigured {
            HasNoPortConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"mapping-path"})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasMappingPathConfigured.class */
        static class HasMappingPathConfigured {
            HasMappingPathConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.rsocket.server", name = {"transport"}, havingValue = Constants.UPGRADE_HEADER_VALUE)
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketServerAutoConfiguration$OnRSocketWebServerCondition$HasWebsocketTransportConfigured.class */
        static class HasWebsocketTransportConfigured {
            HasWebsocketTransportConfigured() {
            }
        }
    }
}
