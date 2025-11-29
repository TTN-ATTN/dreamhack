package org.springframework.boot.autoconfigure.data.jpa;

import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/jpa/EnversRevisionRepositoriesRegistrar.class */
class EnversRevisionRepositoriesRegistrar extends JpaRepositoriesRegistrar {
    EnversRevisionRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesRegistrar, org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableJpaRepositoriesConfiguration.class;
    }

    @EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/jpa/EnversRevisionRepositoriesRegistrar$EnableJpaRepositoriesConfiguration.class */
    private static class EnableJpaRepositoriesConfiguration {
        private EnableJpaRepositoriesConfiguration() {
        }
    }
}
