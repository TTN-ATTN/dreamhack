package org.springframework.boot.autoconfigure.data.mongo;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.MongoRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoRepositoriesRegistrar.class */
class MongoRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    MongoRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableMongoRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableMongoRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new MongoRepositoryConfigurationExtension();
    }

    @EnableMongoRepositories
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/mongo/MongoRepositoriesRegistrar$EnableMongoRepositoriesConfiguration.class */
    private static class EnableMongoRepositoriesConfiguration {
        private EnableMongoRepositoriesConfiguration() {
        }
    }
}
