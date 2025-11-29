package org.springframework.boot.sql.init.dependency;

import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/sql/init/dependency/DependsOnDatabaseInitializationDetector.class */
public interface DependsOnDatabaseInitializationDetector {
    Set<String> detect(ConfigurableListableBeanFactory beanFactory);
}
