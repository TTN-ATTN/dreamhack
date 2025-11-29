package org.springframework.boot.autoconfigure.web.servlet;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.WebListenerRegistrar;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;

@EnableConfigurationProperties({ServerProperties.class})
@AutoConfiguration
@ConditionalOnClass({ServletRequest.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({BeanPostProcessorsRegistrar.class, ServletWebServerFactoryConfiguration.EmbeddedTomcat.class, ServletWebServerFactoryConfiguration.EmbeddedJetty.class, ServletWebServerFactoryConfiguration.EmbeddedUndertow.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration.class */
public class ServletWebServerFactoryAutoConfiguration {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$ForwardedHeaderFilterCustomizer.class */
    interface ForwardedHeaderFilterCustomizer {
        void customize(ForwardedHeaderFilter filter);
    }

    @Bean
    public ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer(ServerProperties serverProperties, ObjectProvider<WebListenerRegistrar> webListenerRegistrars, ObjectProvider<CookieSameSiteSupplier> cookieSameSiteSuppliers) {
        return new ServletWebServerFactoryCustomizer(serverProperties, (List) webListenerRegistrars.orderedStream().collect(Collectors.toList()), (List) cookieSameSiteSuppliers.orderedStream().collect(Collectors.toList()));
    }

    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
    @Bean
    public TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactoryCustomizer(serverProperties);
    }

    @ConditionalOnMissingFilterBean({ForwardedHeaderFilter.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = {"server.forward-headers-strategy"}, havingValue = "framework")
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$ForwardedHeaderFilterConfiguration.class */
    static class ForwardedHeaderFilterConfiguration {
        ForwardedHeaderFilterConfiguration() {
        }

        @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
        @Bean
        ForwardedHeaderFilterCustomizer tomcatForwardedHeaderFilterCustomizer(ServerProperties serverProperties) {
            return filter -> {
                filter.setRelativeRedirects(serverProperties.getTomcat().isUseRelativeRedirects());
            };
        }

        @Bean
        FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter(ObjectProvider<ForwardedHeaderFilterCustomizer> customizerProvider) throws BeansException {
            ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
            customizerProvider.ifAvailable(customizer -> {
                customizer.customize(filter);
            });
            FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter, new ServletRegistrationBean[0]);
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
            registration.setOrder(Integer.MIN_VALUE);
            return registration;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$BeanPostProcessorsRegistrar.class */
    public static class BeanPostProcessorsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
        private ConfigurableListableBeanFactory beanFactory;

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
            }
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
            if (this.beanFactory == null) {
                return;
            }
            registerSyntheticBeanIfMissing(registry, "webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class, WebServerFactoryCustomizerBeanPostProcessor::new);
            registerSyntheticBeanIfMissing(registry, "errorPageRegistrarBeanPostProcessor", ErrorPageRegistrarBeanPostProcessor.class, ErrorPageRegistrarBeanPostProcessor::new);
        }

        private <T> void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry, String name, Class<T> beanClass, Supplier<T> instanceSupplier) throws BeanDefinitionStoreException {
            if (ObjectUtils.isEmpty((Object[]) this.beanFactory.getBeanNamesForType((Class<?>) beanClass, true, false))) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass, instanceSupplier);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(name, beanDefinition);
            }
        }
    }
}
