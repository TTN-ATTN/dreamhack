package org.springframework.boot.context.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindingPostProcessor.class */
public class ConfigurationPropertiesBindingPostProcessor implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean {
    public static final String BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class.getName();
    private ApplicationContext applicationContext;
    private BeanDefinitionRegistry registry;
    private ConfigurationPropertiesBinder binder;

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
        this.binder = ConfigurationPropertiesBinder.get(this.applicationContext);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        bind(ConfigurationPropertiesBean.get(this.applicationContext, bean, beanName));
        return bean;
    }

    private void bind(ConfigurationPropertiesBean bean) {
        if (bean == null || hasBoundValueObject(bean.getName())) {
            return;
        }
        Assert.state(bean.getBindMethod() == ConfigurationPropertiesBean.BindMethod.JAVA_BEAN, "Cannot bind @ConfigurationProperties for bean '" + bean.getName() + "'. Ensure that @ConstructorBinding has not been applied to regular bean");
        try {
            this.binder.bind(bean);
        } catch (Exception ex) {
            throw new ConfigurationPropertiesBindException(bean, ex);
        }
    }

    private boolean hasBoundValueObject(String beanName) {
        return this.registry.containsBeanDefinition(beanName) && ConfigurationPropertiesBean.BindMethod.VALUE_OBJECT.equals(this.registry.getBeanDefinition(beanName).getAttribute(ConfigurationPropertiesBean.BindMethod.class.getName()));
    }

    public static void register(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "Registry must not be null");
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
            BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) ConfigurationPropertiesBindingPostProcessor.class).getBeanDefinition();
            definition.setRole(2);
            registry.registerBeanDefinition(BEAN_NAME, definition);
        }
        ConfigurationPropertiesBinder.register(registry);
    }
}
