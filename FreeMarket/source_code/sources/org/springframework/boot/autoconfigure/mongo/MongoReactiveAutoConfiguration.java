package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration
@ConditionalOnClass({MongoClient.class, Flux.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration.class */
public class MongoReactiveAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public MongoClient reactiveStreamsMongoClient(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
        ReactiveMongoClientFactory factory = new ReactiveMongoClientFactory((List) builderCustomizers.orderedStream().collect(Collectors.toList()));
        return factory.createMongoClient(settings);
    }

    @ConditionalOnMissingBean({MongoClientSettings.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$MongoClientSettingsConfiguration.class */
    static class MongoClientSettingsConfiguration {
        MongoClientSettingsConfiguration() {
        }

        @Bean
        MongoClientSettings mongoClientSettings() {
            return MongoClientSettings.builder().build();
        }

        @Bean
        MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(MongoProperties properties, Environment environment) {
            return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SocketChannel.class, NioEventLoopGroup.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$NettyDriverConfiguration.class */
    static class NettyDriverConfiguration {
        NettyDriverConfiguration() {
        }

        @Bean
        @Order(Integer.MIN_VALUE)
        NettyDriverMongoClientSettingsBuilderCustomizer nettyDriverCustomizer(ObjectProvider<MongoClientSettings> settings) {
            return new NettyDriverMongoClientSettingsBuilderCustomizer(settings);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$NettyDriverMongoClientSettingsBuilderCustomizer.class */
    static final class NettyDriverMongoClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, DisposableBean {
        private final ObjectProvider<MongoClientSettings> settings;
        private volatile EventLoopGroup eventLoopGroup;

        NettyDriverMongoClientSettingsBuilderCustomizer(ObjectProvider<MongoClientSettings> settings) {
            this.settings = settings;
        }

        @Override // org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
        public void customize(MongoClientSettings.Builder builder) {
            if (!isStreamFactoryFactoryDefined(this.settings.getIfAvailable())) {
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                this.eventLoopGroup = eventLoopGroup;
                builder.streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build());
            }
        }

        @Override // org.springframework.beans.factory.DisposableBean
        public void destroy() {
            EventLoopGroup eventLoopGroup = this.eventLoopGroup;
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
                this.eventLoopGroup = null;
            }
        }

        private boolean isStreamFactoryFactoryDefined(MongoClientSettings settings) {
            return (settings == null || settings.getStreamFactoryFactory() == null) ? false : true;
        }
    }
}
