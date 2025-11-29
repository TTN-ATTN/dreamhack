package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoClientFactory.class */
public class MongoClientFactory extends MongoClientFactorySupport<MongoClient> {
    public MongoClientFactory(List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        super(builderCustomizers, MongoClients::create);
    }
}
