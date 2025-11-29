package org.springframework.boot.autoconfigure.orm.jpa;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/orm/jpa/EntityManagerFactoryBuilderCustomizer.class */
public interface EntityManagerFactoryBuilderCustomizer {
    void customize(EntityManagerFactoryBuilder builder);
}
