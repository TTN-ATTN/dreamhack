package org.springframework.boot.context.properties;

import java.util.function.Supplier;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBeanRegistrar.class */
final class ConfigurationPropertiesBeanRegistrar {
    private final BeanDefinitionRegistry registry;
    private final BeanFactory beanFactory;

    ConfigurationPropertiesBeanRegistrar(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.beanFactory = (BeanFactory) this.registry;
    }

    void register(Class<?> type) throws BeanDefinitionStoreException {
        MergedAnnotation<ConfigurationProperties> annotation = MergedAnnotations.from(type, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ConfigurationProperties.class);
        register(type, annotation);
    }

    void register(Class<?> type, MergedAnnotation<ConfigurationProperties> annotation) throws BeanDefinitionStoreException {
        String name = getName(type, annotation);
        if (!containsBeanDefinition(name)) {
            registerBeanDefinition(name, type, annotation);
        }
    }

    private String getName(Class<?> type, MergedAnnotation<ConfigurationProperties> annotation) {
        String prefix = annotation.isPresent() ? annotation.getString("prefix") : "";
        return StringUtils.hasText(prefix) ? prefix + "-" + type.getName() : type.getName();
    }

    private boolean containsBeanDefinition(String name) {
        return containsBeanDefinition(this.beanFactory, name);
    }

    private boolean containsBeanDefinition(BeanFactory beanFactory, String name) {
        if ((beanFactory instanceof ListableBeanFactory) && ((ListableBeanFactory) beanFactory).containsBeanDefinition(name)) {
            return true;
        }
        if (beanFactory instanceof HierarchicalBeanFactory) {
            return containsBeanDefinition(((HierarchicalBeanFactory) beanFactory).getParentBeanFactory(), name);
        }
        return false;
    }

    private void registerBeanDefinition(String beanName, Class<?> type, MergedAnnotation<ConfigurationProperties> annotation) throws BeanDefinitionStoreException {
        Assert.state(annotation.isPresent(), (Supplier<String>) () -> {
            return "No " + ConfigurationProperties.class.getSimpleName() + " annotation found on  '" + type.getName() + "'.";
        });
        this.registry.registerBeanDefinition(beanName, createBeanDefinition(beanName, type));
    }

    private BeanDefinition createBeanDefinition(String beanName, Class<?> type) {
        ConfigurationPropertiesBean.BindMethod bindMethod = ConfigurationPropertiesBean.BindMethod.forType(type);
        RootBeanDefinition definition = new RootBeanDefinition(type);
        definition.setAttribute(ConfigurationPropertiesBean.BindMethod.class.getName(), bindMethod);
        if (bindMethod == ConfigurationPropertiesBean.BindMethod.VALUE_OBJECT) {
            definition.setInstanceSupplier(() -> {
                return createValueObject(beanName, type);
            });
        }
        return definition;
    }

    private Object createValueObject(String beanName, Class<?> beanType) {
        ConfigurationPropertiesBean bean = ConfigurationPropertiesBean.forValueObject(beanType, beanName);
        ConfigurationPropertiesBinder binder = ConfigurationPropertiesBinder.get(this.beanFactory);
        try {
            return binder.bindOrCreate(bean);
        } catch (Exception ex) {
            throw new ConfigurationPropertiesBindException(bean, ex);
        }
    }
}
