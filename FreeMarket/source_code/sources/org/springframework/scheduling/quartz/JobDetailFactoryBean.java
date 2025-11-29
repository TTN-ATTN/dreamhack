package org.springframework.scheduling.quartz;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/JobDetailFactoryBean.class */
public class JobDetailFactoryBean implements FactoryBean<JobDetail>, BeanNameAware, ApplicationContextAware, InitializingBean {

    @Nullable
    private String name;

    @Nullable
    private String group;

    @Nullable
    private Class<? extends Job> jobClass;
    private JobDataMap jobDataMap = new JobDataMap();
    private boolean durability = false;
    private boolean requestsRecovery = false;

    @Nullable
    private String description;

    @Nullable
    private String beanName;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private String applicationContextJobDataKey;

    @Nullable
    private JobDetail jobDetail;

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public JobDataMap getJobDataMap() {
        return this.jobDataMap;
    }

    public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
        getJobDataMap().putAll(jobDataAsMap);
    }

    public void setDurability(boolean durability) {
        this.durability = durability;
    }

    public void setRequestsRecovery(boolean requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setApplicationContextJobDataKey(String applicationContextJobDataKey) {
        this.applicationContextJobDataKey = applicationContextJobDataKey;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Assert.notNull(this.jobClass, "Property 'jobClass' is required");
        if (this.name == null) {
            this.name = this.beanName;
        }
        if (this.group == null) {
            this.group = "DEFAULT";
        }
        if (this.applicationContextJobDataKey != null) {
            if (this.applicationContext == null) {
                throw new IllegalStateException("JobDetailBean needs to be set up in an ApplicationContext to be able to handle an 'applicationContextJobDataKey'");
            }
            getJobDataMap().put(this.applicationContextJobDataKey, this.applicationContext);
        }
        JobDetailImpl jdi = new JobDetailImpl();
        jdi.setName(this.name != null ? this.name : toString());
        jdi.setGroup(this.group);
        jdi.setJobClass(this.jobClass);
        jdi.setJobDataMap(this.jobDataMap);
        jdi.setDurability(this.durability);
        jdi.setRequestsRecovery(this.requestsRecovery);
        jdi.setDescription(this.description);
        this.jobDetail = jdi;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public JobDetail getObject() {
        return this.jobDetail;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return JobDetail.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
