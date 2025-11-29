package org.springframework.scheduling.quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.util.ReflectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/AdaptableJobFactory.class */
public class AdaptableJobFactory implements JobFactory {
    /* JADX INFO: Thrown type has an unknown type hierarchy: org.quartz.SchedulerException */
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        try {
            Object jobObject = createJobInstance(bundle);
            return adaptJob(jobObject);
        } catch (Throwable ex) {
            throw new SchedulerException("Job instantiation failed", ex);
        }
    }

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Class<?> jobClass = bundle.getJobDetail().getJobClass();
        return ReflectionUtils.accessibleConstructor(jobClass, new Class[0]).newInstance(new Object[0]);
    }

    protected Job adaptJob(Object jobObject) throws Exception {
        if (jobObject instanceof Job) {
            return (Job) jobObject;
        }
        if (jobObject instanceof Runnable) {
            return new DelegatingJob((Runnable) jobObject);
        }
        throw new IllegalArgumentException("Unable to execute job class [" + jobObject.getClass().getName() + "]: only [org.quartz.Job] and [java.lang.Runnable] supported.");
    }
}
