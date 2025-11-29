package org.springframework.boot.sql.init.dependency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/sql/init/dependency/BeansOfTypeDetector.class */
class BeansOfTypeDetector {
    private final Set<Class<?>> types;

    BeansOfTypeDetector(Set<Class<?>> types) {
        this.types = types;
    }

    Set<String> detect(ListableBeanFactory beanFactory) {
        Set<String> beanNames = new HashSet<>();
        for (Class<?> type : this.types) {
            try {
                String[] names = beanFactory.getBeanNamesForType(type, true, false);
                Stream map = Arrays.stream(names).map(BeanFactoryUtils::transformedBeanName);
                beanNames.getClass();
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            } catch (Throwable th) {
            }
        }
        return beanNames;
    }
}
