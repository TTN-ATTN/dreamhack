package org.springframework.boot.autoconfigure.data.mongo;

import java.util.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration(proxyBeanMethods = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataConfiguration.class */
class MongoDataConfiguration {
    MongoDataConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    MongoMappingContext mongoMappingContext(ApplicationContext applicationContext, MongoProperties properties, MongoCustomConversions conversions) throws ClassNotFoundException {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        MongoMappingContext context = new MongoMappingContext();
        PropertyMapper.Source sourceFrom = mapper.from((PropertyMapper) properties.isAutoIndexCreation());
        context.getClass();
        sourceFrom.to((v1) -> {
            r1.setAutoIndexCreation(v1);
        });
        context.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class));
        Class<?> strategyClass = properties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }
}
