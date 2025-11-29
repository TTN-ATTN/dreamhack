package org.springframework.scheduling.quartz;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/MethodInvokingJobDetailFactoryBean.class */
public class MethodInvokingJobDetailFactoryBean extends ArgumentConvertingMethodInvoker implements FactoryBean<JobDetail>, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

    @Nullable
    private String name;

    @Nullable
    private String targetBeanName;

    @Nullable
    private String beanName;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private JobDetail jobDetail;
    private String group = "DEFAULT";
    private boolean concurrent = true;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/MethodInvokingJobDetailFactoryBean$StatefulMethodInvokingJob.class */
    public static class StatefulMethodInvokingJob extends MethodInvokingJob {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.util.MethodInvoker
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NoSuchMethodException, ClassNotFoundException {
        prepare();
        String name = this.name != null ? this.name : this.beanName;
        Class<? extends Job> jobClass = this.concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class;
        JobDetailImpl jdi = new JobDetailImpl();
        jdi.setName(name != null ? name : toString());
        jdi.setGroup(this.group);
        jdi.setJobClass(jobClass);
        jdi.setDurability(true);
        jdi.getJobDataMap().put("methodInvoker", this);
        this.jobDetail = jdi;
        postProcessJobDetail(this.jobDetail);
    }

    protected void postProcessJobDetail(JobDetail jobDetail) {
    }

    @Override // org.springframework.util.MethodInvoker
    public Class<?> getTargetClass() throws NoSuchBeanDefinitionException {
        Class<?> targetClass = super.getTargetClass();
        if (targetClass == null && this.targetBeanName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetClass = this.beanFactory.getType(this.targetBeanName);
        }
        return targetClass;
    }

    @Override // org.springframework.util.MethodInvoker
    public Object getTargetObject() throws BeansException {
        Object targetObject = super.getTargetObject();
        if (targetObject == null && this.targetBeanName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetObject = this.beanFactory.getBean(this.targetBeanName);
        }
        return targetObject;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public JobDetail getObject() {
        return this.jobDetail;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends JobDetail> getObjectType() {
        return this.jobDetail != null ? this.jobDetail.getClass() : JobDetail.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/MethodInvokingJobDetailFactoryBean$MethodInvokingJob.class */
    public static class MethodInvokingJob extends QuartzJobBean {
        protected static final Log logger = LogFactory.getLog((Class<?>) MethodInvokingJob.class);

        @Nullable
        private MethodInvoker methodInvoker;

        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: org.quartz.JobExecutionException */
        @Override // org.springframework.scheduling.quartz.QuartzJobBean
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            Assert.state(this.methodInvoker != null, "No MethodInvoker set");
            try {
                context.setResult(this.methodInvoker.invoke());
            } catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof JobExecutionException) {
                    throw ex.getTargetException();
                }
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex.getTargetException());
            } catch (Exception ex2) {
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex2);
            }
        }
    }
}
