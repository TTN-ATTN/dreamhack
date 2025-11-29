package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration
@ConditionalOnClass({MongoClient.class})
@ConditionalOnMissingBean(type = {"org.springframework.data.mongodb.MongoDatabaseFactory"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration.class */
public class MongoAutoConfiguration {
    @ConditionalOnMissingBean({MongoClient.class})
    @Bean
    public MongoClient mongo(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
        return new MongoClientFactory((List) builderCustomizers.orderedStream().collect(Collectors.toList())).createMongoClient(settings);
    }

    @ConditionalOnMissingBean({MongoClientSettings.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration$MongoClientSettingsConfiguration.class */
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
}
