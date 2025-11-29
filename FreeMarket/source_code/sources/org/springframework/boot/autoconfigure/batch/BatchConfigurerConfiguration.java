package org.springframework.boot.autoconfigure.batch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@ConditionalOnClass({PlatformTransactionManager.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean({BatchConfigurer.class})
@ConditionalOnBean({DataSource.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration.class */
class BatchConfigurerConfiguration {
    BatchConfigurerConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"entityManagerFactory"})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration$JdbcBatchConfiguration.class */
    static class JdbcBatchConfiguration {
        JdbcBatchConfiguration() {
        }

        @Bean
        BasicBatchConfigurer batchConfigurer(BatchProperties properties, DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
            return new BasicBatchConfigurer(properties, batchDataSource.getIfAvailable(() -> {
                return dataSource;
            }), transactionManagerCustomizers.getIfAvailable());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({EntityManagerFactory.class})
    @ConditionalOnBean(name = {"entityManagerFactory"})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchConfigurerConfiguration$JpaBatchConfiguration.class */
    static class JpaBatchConfiguration {
        JpaBatchConfiguration() {
        }

        @Bean
        JpaBatchConfigurer batchConfigurer(BatchProperties properties, DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers, EntityManagerFactory entityManagerFactory) {
            return new JpaBatchConfigurer(properties, batchDataSource.getIfAvailable(() -> {
                return dataSource;
            }), transactionManagerCustomizers.getIfAvailable(), entityManagerFactory);
        }
    }
}
