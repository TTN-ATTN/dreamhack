package org.springframework.scheduling.quartz;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/QuartzJobBean.class */
public abstract class QuartzJobBean implements Job {
    protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.quartz.JobExecutionException */
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValues((Map<?, ?>) context.getScheduler().getContext());
            pvs.addPropertyValues((Map<?, ?>) context.getMergedJobDataMap());
            bw.setPropertyValues(pvs, true);
            executeInternal(context);
        } catch (SchedulerException ex) {
            throw new JobExecutionException(ex);
        }
    }
}
