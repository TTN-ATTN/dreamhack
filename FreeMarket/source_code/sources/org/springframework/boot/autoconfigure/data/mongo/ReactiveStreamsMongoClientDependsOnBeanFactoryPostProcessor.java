package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.ReactiveMongoClientFactoryBean;

@Order(Integer.MAX_VALUE)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/ReactiveStreamsMongoClientDependsOnBeanFactoryPostProcessor.class */
public class ReactiveStreamsMongoClientDependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public ReactiveStreamsMongoClientDependsOnBeanFactoryPostProcessor(Class<?>... dependsOn) {
        super((Class<?>) MongoClient.class, (Class<? extends FactoryBean<?>>) ReactiveMongoClientFactoryBean.class, dependsOn);
    }
}
