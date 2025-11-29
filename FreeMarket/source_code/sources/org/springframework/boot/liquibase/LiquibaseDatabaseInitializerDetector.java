package org.springframework.boot.liquibase;

import java.util.Collections;
import java.util.Set;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/liquibase/LiquibaseDatabaseInitializerDetector.class */
class LiquibaseDatabaseInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {
    LiquibaseDatabaseInitializerDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(SpringLiquibase.class);
    }
}
