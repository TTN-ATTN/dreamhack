package org.springframework.boot.jdbc;

import java.util.Collections;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/AbstractDataSourceInitializerDatabaseInitializerDetector.class */
class AbstractDataSourceInitializerDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    private static final int PRECEDENCE = 2147483547;

    AbstractDataSourceInitializerDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(AbstractDataSourceInitializer.class);
    }

    @Override // org.springframework.boot.sql.init.dependency.DatabaseInitializerDetector, org.springframework.core.Ordered
    public int getOrder() {
        return PRECEDENCE;
    }
}
