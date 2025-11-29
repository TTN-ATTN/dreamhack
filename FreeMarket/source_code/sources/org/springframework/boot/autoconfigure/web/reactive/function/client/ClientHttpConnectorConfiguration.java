package org.springframework.boot.autoconfigure.web.reactive.function.client;

import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.eclipse.jetty.reactive.client.ReactiveRequest;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.reactor.netty.ReactorNettyConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.http.client.HttpClient;

@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorConfiguration.class */
class ClientHttpConnectorConfiguration {
    ClientHttpConnectorConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpClient.class})
    @ConditionalOnMissingBean({ClientHttpConnector.class})
    @Import({ReactorNettyConfigurations.ReactorResourceFactoryConfiguration.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorConfiguration$ReactorNetty.class */
    static class ReactorNetty {
        ReactorNetty() {
        }

        @Bean
        @Lazy
        ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory reactorResourceFactory, ObjectProvider<ReactorNettyHttpClientMapper> mapperProvider) {
            ReactorNettyHttpClientMapper mapper = mapperProvider.orderedStream().reduce((before, after) -> {
                return client -> {
                    return after.configure(before.configure(client));
                };
            }).orElse(client -> {
                return client;
            });
            mapper.getClass();
            return new ReactorClientHttpConnector(reactorResourceFactory, mapper::configure);
        }
    }

    @ConditionalOnMissingBean({ClientHttpConnector.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ReactiveRequest.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorConfiguration$JettyClient.class */
    static class JettyClient {
        JettyClient() {
        }

        @ConditionalOnMissingBean
        @Bean
        JettyResourceFactory jettyClientResourceFactory() {
            return new JettyResourceFactory();
        }

        @Bean
        @Lazy
        JettyClientHttpConnector jettyClientHttpConnector(JettyResourceFactory jettyResourceFactory) {
            org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient(new SslContextFactory.Client());
            return new JettyClientHttpConnector(httpClient, jettyResourceFactory);
        }
    }

    @ConditionalOnMissingBean({ClientHttpConnector.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HttpAsyncClients.class, AsyncRequestProducer.class, ReactiveResponseConsumer.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorConfiguration$HttpClient5.class */
    static class HttpClient5 {
        HttpClient5() {
        }

        @Bean
        @Lazy
        HttpComponentsClientHttpConnector httpComponentsClientHttpConnector() {
            return new HttpComponentsClientHttpConnector();
        }
    }
}
