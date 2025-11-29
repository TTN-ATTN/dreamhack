package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Map;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernatePropertiesCustomizer.class */
public interface HibernatePropertiesCustomizer {
    void customize(Map<String, Object> hibernateProperties);
}
