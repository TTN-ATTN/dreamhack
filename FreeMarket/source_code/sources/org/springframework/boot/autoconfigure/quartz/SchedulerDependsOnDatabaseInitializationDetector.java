package org.springframework.boot.autoconfigure.quartz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.quartz.Scheduler;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/quartz/SchedulerDependsOnDatabaseInitializationDetector.class */
class SchedulerDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    SchedulerDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return new HashSet(Arrays.asList(Scheduler.class, SchedulerFactoryBean.class));
    }
}
