package org.springframework.boot.flyway;

import java.util.Collections;
import java.util.Set;
import org.flywaydb.core.Flyway;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/flyway/FlywayDatabaseInitializerDetector.class */
class FlywayDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    FlywayDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(Flyway.class);
    }
}
