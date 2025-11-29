package org.springframework.boot.autoconfigure.reactor.netty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/reactor/netty/ReactorNettyConfigurations.class */
public final class ReactorNettyConfigurations {
    private ReactorNettyConfigurations() {
    }

    @EnableConfigurationProperties({ReactorNettyProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/reactor/netty/ReactorNettyConfigurations$ReactorResourceFactoryConfiguration.class */
    public static class ReactorResourceFactoryConfiguration {
        @ConditionalOnMissingBean
        @Bean
        ReactorResourceFactory reactorResourceFactory(ReactorNettyProperties configurationProperties) {
            ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
            if (configurationProperties.getShutdownQuietPeriod() != null) {
                reactorResourceFactory.setShutdownQuietPeriod(configurationProperties.getShutdownQuietPeriod());
            }
            return reactorResourceFactory;
        }
    }
}
