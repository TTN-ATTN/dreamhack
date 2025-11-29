package org.springframework.scheduling.quartz;

import java.util.Date;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/SimpleTriggerFactoryBean.class */
public class SimpleTriggerFactoryBean implements FactoryBean<SimpleTrigger>, BeanNameAware, InitializingBean {
    private static final Constants constants = new Constants(SimpleTrigger.class);

    @Nullable
    private String name;

    @Nullable
    private String group;

    @Nullable
    private JobDetail jobDetail;

    @Nullable
    private Date startTime;
    private long startDelay;
    private long repeatInterval;
    private int priority;
    private int misfireInstruction;

    @Nullable
    private String description;

    @Nullable
    private String beanName;

    @Nullable
    private SimpleTrigger simpleTrigger;
    private JobDataMap jobDataMap = new JobDataMap();
    private int repeatCount = -1;

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public JobDataMap getJobDataMap() {
        return this.jobDataMap;
    }

    public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
        this.jobDataMap.putAll(jobDataAsMap);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setStartDelay(long startDelay) {
        Assert.isTrue(startDelay >= 0, "Start delay cannot be negative");
        this.startDelay = startDelay;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

    public void setMisfireInstructionName(String constantName) {
        this.misfireInstruction = constants.asNumber(constantName).intValue();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.name == null) {
            this.name = this.beanName;
        }
        if (this.group == null) {
            this.group = "DEFAULT";
        }
        if (this.jobDetail != null) {
            this.jobDataMap.put("jobDetail", this.jobDetail);
        }
        if (this.startDelay > 0 || this.startTime == null) {
            this.startTime = new Date(System.currentTimeMillis() + this.startDelay);
        }
        SimpleTriggerImpl sti = new SimpleTriggerImpl();
        sti.setName(this.name != null ? this.name : toString());
        sti.setGroup(this.group);
        if (this.jobDetail != null) {
            sti.setJobKey(this.jobDetail.getKey());
        }
        sti.setJobDataMap(this.jobDataMap);
        sti.setStartTime(this.startTime);
        sti.setRepeatInterval(this.repeatInterval);
        sti.setRepeatCount(this.repeatCount);
        sti.setPriority(this.priority);
        sti.setMisfireInstruction(this.misfireInstruction);
        sti.setDescription(this.description);
        this.simpleTrigger = sti;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public SimpleTrigger getObject() {
        return this.simpleTrigger;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return SimpleTrigger.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
