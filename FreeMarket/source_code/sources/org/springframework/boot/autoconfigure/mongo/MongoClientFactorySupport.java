package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoClientFactorySupport.class */
public abstract class MongoClientFactorySupport<T> {
    private final List<MongoClientSettingsBuilderCustomizer> builderCustomizers;
    private final BiFunction<MongoClientSettings, MongoDriverInformation, T> clientCreator;

    protected MongoClientFactorySupport(List<MongoClientSettingsBuilderCustomizer> builderCustomizers, BiFunction<MongoClientSettings, MongoDriverInformation, T> clientCreator) {
        this.builderCustomizers = builderCustomizers != null ? builderCustomizers : Collections.emptyList();
        this.clientCreator = clientCreator;
    }

    public T createMongoClient(MongoClientSettings settings) {
        MongoClientSettings.Builder targetSettings = MongoClientSettings.builder(settings);
        customize(targetSettings);
        return this.clientCreator.apply(targetSettings.build(), driverInformation());
    }

    private void customize(MongoClientSettings.Builder builder) {
        for (MongoClientSettingsBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
    }

    private MongoDriverInformation driverInformation() {
        return MongoDriverInformation.builder(MongoDriverInformation.builder().build()).driverName("spring-boot").build();
    }
}
