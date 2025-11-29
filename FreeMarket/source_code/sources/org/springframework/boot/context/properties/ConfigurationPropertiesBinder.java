package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.BoundPropertiesTrackingBindHandler;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBinder.class */
class ConfigurationPropertiesBinder {
    private static final String BEAN_NAME = "org.springframework.boot.context.internalConfigurationPropertiesBinder";
    private static final String FACTORY_BEAN_NAME = "org.springframework.boot.context.internalConfigurationPropertiesBinderFactory";
    private static final String VALIDATOR_BEAN_NAME = "configurationPropertiesValidator";
    private final ApplicationContext applicationContext;
    private final PropertySources propertySources;
    private final Validator configurationPropertiesValidator;
    private final boolean jsr303Present;
    private volatile Validator jsr303Validator;
    private volatile Binder binder;

    ConfigurationPropertiesBinder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.propertySources = new PropertySourcesDeducer(applicationContext).getPropertySources();
        this.configurationPropertiesValidator = getConfigurationPropertiesValidator(applicationContext);
        this.jsr303Present = ConfigurationPropertiesJsr303Validator.isJsr303Present(applicationContext);
    }

    BindResult<?> bind(ConfigurationPropertiesBean propertiesBean) {
        Bindable<?> target = propertiesBean.asBindTarget();
        ConfigurationProperties annotation = propertiesBean.getAnnotation();
        BindHandler bindHandler = getBindHandler(target, annotation);
        return getBinder().bind(annotation.prefix(), target, bindHandler);
    }

    Object bindOrCreate(ConfigurationPropertiesBean propertiesBean) {
        Bindable<?> target = propertiesBean.asBindTarget();
        ConfigurationProperties annotation = propertiesBean.getAnnotation();
        BindHandler bindHandler = getBindHandler(target, annotation);
        return getBinder().bindOrCreate(annotation.prefix(), target, bindHandler);
    }

    private Validator getConfigurationPropertiesValidator(ApplicationContext applicationContext) {
        if (applicationContext.containsBean("configurationPropertiesValidator")) {
            return (Validator) applicationContext.getBean("configurationPropertiesValidator", Validator.class);
        }
        return null;
    }

    private <T> BindHandler getBindHandler(Bindable<T> target, ConfigurationProperties annotation) {
        List<Validator> validators = getValidators(target);
        BindHandler handler = new ConfigurationPropertiesBindHandler(getHandler());
        if (annotation.ignoreInvalidFields()) {
            handler = new IgnoreErrorsBindHandler(handler);
        }
        if (!annotation.ignoreUnknownFields()) {
            UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
            handler = new NoUnboundElementsBindHandler(handler, filter);
        }
        if (!validators.isEmpty()) {
            handler = new ValidationBindHandler(handler, (Validator[]) validators.toArray(new Validator[0]));
        }
        for (ConfigurationPropertiesBindHandlerAdvisor advisor : getBindHandlerAdvisors()) {
            handler = advisor.apply(handler);
        }
        return handler;
    }

    private IgnoreTopLevelConverterNotFoundBindHandler getHandler() {
        BoundConfigurationProperties bound = BoundConfigurationProperties.get(this.applicationContext);
        if (bound == null) {
            return new IgnoreTopLevelConverterNotFoundBindHandler();
        }
        bound.getClass();
        return new IgnoreTopLevelConverterNotFoundBindHandler(new BoundPropertiesTrackingBindHandler(bound::add));
    }

    private List<Validator> getValidators(Bindable<?> target) {
        List<Validator> validators = new ArrayList<>(3);
        if (this.configurationPropertiesValidator != null) {
            validators.add(this.configurationPropertiesValidator);
        }
        if (this.jsr303Present && target.getAnnotation(Validated.class) != null) {
            validators.add(getJsr303Validator());
        }
        if (target.getValue() != null && (target.getValue().get() instanceof Validator)) {
            validators.add((Validator) target.getValue().get());
        }
        return validators;
    }

    private Validator getJsr303Validator() {
        if (this.jsr303Validator == null) {
            this.jsr303Validator = new ConfigurationPropertiesJsr303Validator(this.applicationContext);
        }
        return this.jsr303Validator;
    }

    private List<ConfigurationPropertiesBindHandlerAdvisor> getBindHandlerAdvisors() {
        return (List) this.applicationContext.getBeanProvider(ConfigurationPropertiesBindHandlerAdvisor.class).orderedStream().collect(Collectors.toList());
    }

    private Binder getBinder() {
        if (this.binder == null) {
            this.binder = new Binder(getConfigurationPropertySources(), getPropertySourcesPlaceholdersResolver(), getConversionServices(), getPropertyEditorInitializer(), (BindHandler) null, ConfigurationPropertiesBindConstructorProvider.INSTANCE);
        }
        return this.binder;
    }

    private Iterable<ConfigurationPropertySource> getConfigurationPropertySources() {
        return ConfigurationPropertySources.from(this.propertySources);
    }

    private PropertySourcesPlaceholdersResolver getPropertySourcesPlaceholdersResolver() {
        return new PropertySourcesPlaceholdersResolver(this.propertySources);
    }

    private List<ConversionService> getConversionServices() {
        return new ConversionServiceDeducer(this.applicationContext).getConversionServices();
    }

    private Consumer<PropertyEditorRegistry> getPropertyEditorInitializer() throws IllegalStateException {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
            beanFactory.getClass();
            return beanFactory::copyRegisteredEditorsTo;
        }
        return null;
    }

    static void register(BeanDefinitionRegistry registry) throws BeanDefinitionValidationException, BeanDefinitionStoreException {
        if (!registry.containsBeanDefinition(FACTORY_BEAN_NAME)) {
            BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) Factory.class).getBeanDefinition();
            definition.setRole(2);
            registry.registerBeanDefinition(FACTORY_BEAN_NAME, definition);
        }
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
            BeanDefinition definition2 = BeanDefinitionBuilder.rootBeanDefinition(ConfigurationPropertiesBinder.class, () -> {
                return ((Factory) ((BeanFactory) registry).getBean(FACTORY_BEAN_NAME, Factory.class)).create();
            }).getBeanDefinition();
            definition2.setRole(2);
            registry.registerBeanDefinition(BEAN_NAME, definition2);
        }
    }

    static ConfigurationPropertiesBinder get(BeanFactory beanFactory) {
        return (ConfigurationPropertiesBinder) beanFactory.getBean(BEAN_NAME, ConfigurationPropertiesBinder.class);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBinder$Factory.class */
    static class Factory implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        Factory() {
        }

        @Override // org.springframework.context.ApplicationContextAware
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ConfigurationPropertiesBinder create() {
            return new ConfigurationPropertiesBinder(this.applicationContext);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBinder$ConfigurationPropertiesBindHandler.class */
    private static class ConfigurationPropertiesBindHandler extends AbstractBindHandler {
        ConfigurationPropertiesBindHandler(BindHandler handler) {
            super(handler);
        }

        @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
        public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
            return isConfigurationProperties(target.getType().resolve()) ? target.withBindRestrictions(Bindable.BindRestriction.NO_DIRECT_PROPERTY) : target;
        }

        private boolean isConfigurationProperties(Class<?> target) {
            return target != null && MergedAnnotations.from(target).isPresent(ConfigurationProperties.class);
        }
    }
}
