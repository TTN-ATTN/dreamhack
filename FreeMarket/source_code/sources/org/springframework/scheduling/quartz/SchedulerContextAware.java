package org.springframework.scheduling.quartz;

import org.quartz.SchedulerContext;
import org.springframework.beans.factory.Aware;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/SchedulerContextAware.class */
public interface SchedulerContextAware extends Aware {
    void setSchedulerContext(SchedulerContext schedulerContext);
}
