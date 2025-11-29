package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Order(Integer.MAX_VALUE)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoClientDependsOnBeanFactoryPostProcessor.class */
public class MongoClientDependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public MongoClientDependsOnBeanFactoryPostProcessor(Class<?>... dependsOn) {
        super((Class<?>) MongoClient.class, (Class<? extends FactoryBean<?>>) MongoClientFactoryBean.class, dependsOn);
    }
}
