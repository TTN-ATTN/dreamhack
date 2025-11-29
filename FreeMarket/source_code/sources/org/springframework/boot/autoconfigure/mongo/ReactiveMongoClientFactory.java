package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/ReactiveMongoClientFactory.class */
public class ReactiveMongoClientFactory extends MongoClientFactorySupport<MongoClient> {
    public ReactiveMongoClientFactory(List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        super(builderCustomizers, MongoClients::create);
    }
}
