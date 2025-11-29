package org.springframework.boot.autoconfigure.integration;

import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.IntegrationComponentScanRegistrar;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfigurationScanRegistrar.class */
class IntegrationAutoConfigurationScanRegistrar extends IntegrationComponentScanRegistrar implements BeanFactoryAware {
    private BeanFactory beanFactory;

    IntegrationAutoConfigurationScanRegistrar() {
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
        super.registerBeanDefinitions(AnnotationMetadata.introspect(IntegrationComponentScanConfiguration.class), registry);
    }

    protected Collection<String> getBasePackages(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        return AutoConfigurationPackages.has(this.beanFactory) ? AutoConfigurationPackages.get(this.beanFactory) : Collections.emptyList();
    }

    @IntegrationComponentScan
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfigurationScanRegistrar$IntegrationComponentScanConfiguration.class */
    private static class IntegrationComponentScanConfiguration {
        private IntegrationComponentScanConfiguration() {
        }
    }
}
