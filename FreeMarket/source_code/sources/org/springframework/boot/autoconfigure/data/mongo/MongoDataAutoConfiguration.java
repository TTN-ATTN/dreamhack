package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableConfigurationProperties({MongoProperties.class})
@AutoConfiguration(after = {MongoAutoConfiguration.class})
@ConditionalOnClass({MongoClient.class, MongoTemplate.class})
@Import({MongoDataConfiguration.class, MongoDatabaseFactoryConfiguration.class, MongoDatabaseFactoryDependentConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoDataAutoConfiguration.class */
public class MongoDataAutoConfiguration {
}
