package org.springframework.scheduling.quartz;

import java.util.Map;
import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/SpringBeanJobFactory.class */
public class SpringBeanJobFactory extends AdaptableJobFactory implements ApplicationContextAware, SchedulerContextAware {

    @Nullable
    private String[] ignoredUnknownProperties;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private SchedulerContext schedulerContext;

    public void setIgnoredUnknownProperties(String... ignoredUnknownProperties) {
        this.ignoredUnknownProperties = ignoredUnknownProperties;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.scheduling.quartz.SchedulerContextAware
    public void setSchedulerContext(SchedulerContext schedulerContext) {
        this.schedulerContext = schedulerContext;
    }

    @Override // org.springframework.scheduling.quartz.AdaptableJobFactory
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object objCreateJobInstance;
        if (this.applicationContext != null) {
            objCreateJobInstance = this.applicationContext.getAutowireCapableBeanFactory().createBean(bundle.getJobDetail().getJobClass(), 3, false);
        } else {
            objCreateJobInstance = super.createJobInstance(bundle);
        }
        Object job = objCreateJobInstance;
        if (isEligibleForPropertyPopulation(job)) {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
            MutablePropertyValues pvs = new MutablePropertyValues();
            if (this.schedulerContext != null) {
                pvs.addPropertyValues((Map<?, ?>) this.schedulerContext);
            }
            pvs.addPropertyValues((Map<?, ?>) bundle.getJobDetail().getJobDataMap());
            pvs.addPropertyValues((Map<?, ?>) bundle.getTrigger().getJobDataMap());
            if (this.ignoredUnknownProperties != null) {
                for (String propName : this.ignoredUnknownProperties) {
                    if (pvs.contains(propName) && !bw.isWritableProperty(propName)) {
                        pvs.removePropertyValue(propName);
                    }
                }
                bw.setPropertyValues(pvs);
            } else {
                bw.setPropertyValues(pvs, true);
            }
        }
        return job;
    }

    protected boolean isEligibleForPropertyPopulation(Object jobObject) {
        return !(jobObject instanceof QuartzJobBean);
    }
}
