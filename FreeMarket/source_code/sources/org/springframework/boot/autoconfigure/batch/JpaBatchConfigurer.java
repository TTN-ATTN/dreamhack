package org.springframework.boot.autoconfigure.batch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/JpaBatchConfigurer.class */
public class JpaBatchConfigurer extends BasicBatchConfigurer {
    private static final Log logger = LogFactory.getLog((Class<?>) JpaBatchConfigurer.class);
    private final EntityManagerFactory entityManagerFactory;

    protected JpaBatchConfigurer(BatchProperties properties, DataSource dataSource, TransactionManagerCustomizers transactionManagerCustomizers, EntityManagerFactory entityManagerFactory) {
        super(properties, dataSource, transactionManagerCustomizers);
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override // org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
    protected String determineIsolationLevel() {
        String name = super.determineIsolationLevel();
        if (name != null) {
            return name;
        }
        logger.warn("JPA does not support custom isolation levels, so locks may not be taken when launching Jobs. To silence this warning, set 'spring.batch.jdbc.isolation-level-for-create' to 'default'.");
        return BatchProperties.Isolation.DEFAULT.toIsolationName();
    }

    @Override // org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
    protected PlatformTransactionManager createTransactionManager() {
        return new JpaTransactionManager(this.entityManagerFactory);
    }
}
