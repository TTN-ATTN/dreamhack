package org.springframework.boot.web.reactive.context;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/AnnotationConfigReactiveWebServerApplicationContext.class */
public class AnnotationConfigReactiveWebServerApplicationContext extends ReactiveWebServerApplicationContext implements AnnotationConfigRegistry {
    private final AnnotatedBeanDefinitionReader reader;
    private final ClassPathBeanDefinitionScanner scanner;
    private final Set<Class<?>> annotatedClasses;
    private String[] basePackages;

    public AnnotationConfigReactiveWebServerApplicationContext() {
        this.annotatedClasses = new LinkedHashSet();
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigReactiveWebServerApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
        this.annotatedClasses = new LinkedHashSet();
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigReactiveWebServerApplicationContext(Class<?>... annotatedClasses) {
        this();
        register(annotatedClasses);
        refresh();
    }

    public AnnotationConfigReactiveWebServerApplicationContext(String... basePackages) {
        this();
        scan(basePackages);
        refresh();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
    }

    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public final void register(Class<?>... annotatedClasses) {
        Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
        this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public final void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        this.basePackages = basePackages;
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected void prepareRefresh() {
        this.scanner.clearCache();
        super.prepareRefresh();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeanDefinitionStoreException {
        super.postProcessBeanFactory(beanFactory);
        if (!ObjectUtils.isEmpty((Object[]) this.basePackages)) {
            this.scanner.scan(this.basePackages);
        }
        if (!this.annotatedClasses.isEmpty()) {
            this.reader.register(ClassUtils.toClassArray(this.annotatedClasses));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/AnnotationConfigReactiveWebServerApplicationContext$Factory.class */
    static class Factory implements ApplicationContextFactory {
        Factory() {
        }

        @Override // org.springframework.boot.ApplicationContextFactory
        public Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
            if (webApplicationType != WebApplicationType.REACTIVE) {
                return null;
            }
            return ApplicationReactiveWebEnvironment.class;
        }

        @Override // org.springframework.boot.ApplicationContextFactory
        public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
            if (webApplicationType != WebApplicationType.REACTIVE) {
                return null;
            }
            return new ApplicationReactiveWebEnvironment();
        }

        @Override // org.springframework.boot.ApplicationContextFactory
        public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
            if (webApplicationType != WebApplicationType.REACTIVE) {
                return null;
            }
            return new AnnotationConfigReactiveWebServerApplicationContext();
        }
    }
}
