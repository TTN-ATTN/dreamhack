package org.springframework.boot.sql.init.dependency;

import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/sql/init/dependency/DatabaseInitializerDetector.class */
public interface DatabaseInitializerDetector extends Ordered {
    Set<String> detect(ConfigurableListableBeanFactory beanFactory);

    default void detectionComplete(ConfigurableListableBeanFactory beanFactory, Set<String> dataSourceInitializerNames) {
    }

    @Override // org.springframework.core.Ordered
    default int getOrder() {
        return 0;
    }
}
