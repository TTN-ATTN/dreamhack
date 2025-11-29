package org.springframework.boot.autoconfigure.orm.jpa;

import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/orm/jpa/EntityManagerFactoryDependsOnPostProcessor.class */
public class EntityManagerFactoryDependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public EntityManagerFactoryDependsOnPostProcessor(String... dependsOn) {
        super((Class<?>) EntityManagerFactory.class, (Class<? extends FactoryBean<?>>) AbstractEntityManagerFactoryBean.class, dependsOn);
    }

    public EntityManagerFactoryDependsOnPostProcessor(Class<?>... dependsOn) {
        super((Class<?>) EntityManagerFactory.class, (Class<? extends FactoryBean<?>>) AbstractEntityManagerFactoryBean.class, dependsOn);
    }
}
