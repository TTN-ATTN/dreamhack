package org.springframework.boot.autoconfigure.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.boot.jackson.JsonMixinModule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@AutoConfiguration
@ConditionalOnClass({ObjectMapper.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration.class */
public class JacksonAutoConfiguration {
    private static final Map<?, Boolean> FEATURE_DEFAULTS;

    static {
        Map<Object, Boolean> featureDefaults = new HashMap<>();
        featureDefaults.put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        featureDefaults.put(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        FEATURE_DEFAULTS = Collections.unmodifiableMap(featureDefaults);
    }

    @Bean
    public JsonComponentModule jsonComponentModule() {
        return new JsonComponentModule();
    }

    @Bean
    public JsonMixinModule jsonMixinModule(ApplicationContext context) {
        List<String> packages = AutoConfigurationPackages.has(context) ? AutoConfigurationPackages.get(context) : Collections.emptyList();
        return new JsonMixinModule(context, packages);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Jackson2ObjectMapperBuilder.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration$JacksonObjectMapperConfiguration.class */
    static class JacksonObjectMapperConfiguration {
        JacksonObjectMapperConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @Primary
        ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
            return builder.createXmlMapper(false).build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ParameterNamesModule.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration$ParameterNamesModuleConfiguration.class */
    static class ParameterNamesModuleConfiguration {
        ParameterNamesModuleConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        ParameterNamesModule parameterNamesModule() {
            return new ParameterNamesModule(JsonCreator.Mode.DEFAULT);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Jackson2ObjectMapperBuilder.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration$JacksonObjectMapperBuilderConfiguration.class */
    static class JacksonObjectMapperBuilderConfiguration {
        JacksonObjectMapperBuilderConfiguration() {
        }

        @ConditionalOnMissingBean
        @Scope("prototype")
        @Bean
        Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder(ApplicationContext applicationContext, List<Jackson2ObjectMapperBuilderCustomizer> customizers) {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.applicationContext(applicationContext);
            customize(builder, customizers);
            return builder;
        }

        private void customize(Jackson2ObjectMapperBuilder builder, List<Jackson2ObjectMapperBuilderCustomizer> customizers) {
            for (Jackson2ObjectMapperBuilderCustomizer customizer : customizers) {
                customizer.customize(builder);
            }
        }
    }

    @EnableConfigurationProperties({JacksonProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Jackson2ObjectMapperBuilder.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration.class */
    static class Jackson2ObjectMapperBuilderCustomizerConfiguration {
        Jackson2ObjectMapperBuilderCustomizerConfiguration() {
        }

        @Bean
        StandardJackson2ObjectMapperBuilderCustomizer standardJacksonObjectMapperBuilderCustomizer(ApplicationContext applicationContext, JacksonProperties jacksonProperties) {
            return new StandardJackson2ObjectMapperBuilderCustomizer(applicationContext, jacksonProperties);
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration$StandardJackson2ObjectMapperBuilderCustomizer.class */
        static final class StandardJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer, Ordered {
            private final ApplicationContext applicationContext;
            private final JacksonProperties jacksonProperties;

            StandardJackson2ObjectMapperBuilderCustomizer(ApplicationContext applicationContext, JacksonProperties jacksonProperties) {
                this.applicationContext = applicationContext;
                this.jacksonProperties = jacksonProperties;
            }

            @Override // org.springframework.core.Ordered
            public int getOrder() {
                return 0;
            }

            @Override // org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
            public void customize(Jackson2ObjectMapperBuilder builder) throws LinkageError {
                if (this.jacksonProperties.getDefaultPropertyInclusion() != null) {
                    builder.serializationInclusion(this.jacksonProperties.getDefaultPropertyInclusion());
                }
                if (this.jacksonProperties.getTimeZone() != null) {
                    builder.timeZone(this.jacksonProperties.getTimeZone());
                }
                configureFeatures(builder, JacksonAutoConfiguration.FEATURE_DEFAULTS);
                configureVisibility(builder, this.jacksonProperties.getVisibility());
                configureFeatures(builder, this.jacksonProperties.getDeserialization());
                configureFeatures(builder, this.jacksonProperties.getSerialization());
                configureFeatures(builder, this.jacksonProperties.getMapper());
                configureFeatures(builder, this.jacksonProperties.getParser());
                configureFeatures(builder, this.jacksonProperties.getGenerator());
                configureDateFormat(builder);
                configurePropertyNamingStrategy(builder);
                configureModules(builder);
                configureLocale(builder);
                configureDefaultLeniency(builder);
                configureConstructorDetector(builder);
            }

            private void configureFeatures(Jackson2ObjectMapperBuilder builder, Map<?, Boolean> features) {
                features.forEach((feature, value) -> {
                    if (value != null) {
                        if (value.booleanValue()) {
                            builder.featuresToEnable(feature);
                        } else {
                            builder.featuresToDisable(feature);
                        }
                    }
                });
            }

            private void configureVisibility(Jackson2ObjectMapperBuilder builder, Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilities) {
                builder.getClass();
                visibilities.forEach(builder::visibility);
            }

            private void configureDateFormat(Jackson2ObjectMapperBuilder builder) throws LinkageError {
                String dateFormat = this.jacksonProperties.getDateFormat();
                if (dateFormat != null) {
                    try {
                        Class<?> dateFormatClass = ClassUtils.forName(dateFormat, null);
                        builder.dateFormat((DateFormat) BeanUtils.instantiateClass(dateFormatClass));
                    } catch (ClassNotFoundException e) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                        TimeZone timeZone = this.jacksonProperties.getTimeZone();
                        if (timeZone == null) {
                            timeZone = new ObjectMapper().getSerializationConfig().getTimeZone();
                        }
                        simpleDateFormat.setTimeZone(timeZone);
                        builder.dateFormat(simpleDateFormat);
                    }
                }
            }

            private void configurePropertyNamingStrategy(Jackson2ObjectMapperBuilder builder) {
                String strategy = this.jacksonProperties.getPropertyNamingStrategy();
                if (strategy != null) {
                    try {
                        configurePropertyNamingStrategyClass(builder, ClassUtils.forName(strategy, null));
                    } catch (ClassNotFoundException e) {
                        configurePropertyNamingStrategyField(builder, strategy);
                    }
                }
            }

            private void configurePropertyNamingStrategyClass(Jackson2ObjectMapperBuilder builder, Class<?> propertyNamingStrategyClass) {
                builder.propertyNamingStrategy((PropertyNamingStrategy) BeanUtils.instantiateClass(propertyNamingStrategyClass));
            }

            private void configurePropertyNamingStrategyField(Jackson2ObjectMapperBuilder builder, String fieldName) {
                Field field = findPropertyNamingStrategyField(fieldName);
                Assert.notNull(field, (Supplier<String>) () -> {
                    return "Constant named '" + fieldName + "' not found";
                });
                try {
                    builder.propertyNamingStrategy((PropertyNamingStrategy) field.get(null));
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }

            private Field findPropertyNamingStrategyField(String fieldName) {
                try {
                    return ReflectionUtils.findField(PropertyNamingStrategies.class, fieldName, PropertyNamingStrategy.class);
                } catch (NoClassDefFoundError e) {
                    return ReflectionUtils.findField(PropertyNamingStrategy.class, fieldName, PropertyNamingStrategy.class);
                }
            }

            private void configureModules(Jackson2ObjectMapperBuilder builder) {
                Collection<Module> moduleBeans = getBeans(this.applicationContext, Module.class);
                builder.modulesToInstall((Module[]) moduleBeans.toArray(new Module[0]));
            }

            private void configureLocale(Jackson2ObjectMapperBuilder builder) {
                Locale locale = this.jacksonProperties.getLocale();
                if (locale != null) {
                    builder.locale(locale);
                }
            }

            private void configureDefaultLeniency(Jackson2ObjectMapperBuilder builder) {
                Boolean defaultLeniency = this.jacksonProperties.getDefaultLeniency();
                if (defaultLeniency != null) {
                    builder.postConfigurer(objectMapper -> {
                        objectMapper.setDefaultLeniency(defaultLeniency);
                    });
                }
            }

            private void configureConstructorDetector(Jackson2ObjectMapperBuilder builder) {
                JacksonProperties.ConstructorDetectorStrategy strategy = this.jacksonProperties.getConstructorDetector();
                if (strategy != null) {
                    builder.postConfigurer(objectMapper -> {
                        switch (strategy) {
                            case USE_PROPERTIES_BASED:
                                objectMapper.setConstructorDetector(ConstructorDetector.USE_PROPERTIES_BASED);
                                break;
                            case USE_DELEGATING:
                                objectMapper.setConstructorDetector(ConstructorDetector.USE_DELEGATING);
                                break;
                            case EXPLICIT_ONLY:
                                objectMapper.setConstructorDetector(ConstructorDetector.EXPLICIT_ONLY);
                                break;
                            default:
                                objectMapper.setConstructorDetector(ConstructorDetector.DEFAULT);
                                break;
                        }
                    });
                }
            }

            private static <T> Collection<T> getBeans(ListableBeanFactory beanFactory, Class<T> type) {
                return BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, type).values();
            }
        }
    }
}
