package org.springframework.boot.autoconfigure.thymeleaf;

import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations.class */
class TemplateEngineConfigurations {
    TemplateEngineConfigurations() {
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations$DefaultTemplateEngineConfiguration.class */
    static class DefaultTemplateEngineConfiguration {
        DefaultTemplateEngineConfiguration() {
        }

        @ConditionalOnMissingBean({ISpringTemplateEngine.class})
        @Bean
        SpringTemplateEngine templateEngine(ThymeleafProperties properties, ObjectProvider<ITemplateResolver> templateResolvers, ObjectProvider<IDialect> dialects) {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setEnableSpringELCompiler(properties.isEnableSpringElCompiler());
            engine.setRenderHiddenMarkersBeforeCheckboxes(properties.isRenderHiddenMarkersBeforeCheckboxes());
            Stream<ITemplateResolver> streamOrderedStream = templateResolvers.orderedStream();
            engine.getClass();
            streamOrderedStream.forEach(engine::addTemplateResolver);
            Stream<IDialect> streamOrderedStream2 = dialects.orderedStream();
            engine.getClass();
            streamOrderedStream2.forEach(engine::addDialect);
            return engine;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = {"spring.thymeleaf.enabled"}, matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations$ReactiveTemplateEngineConfiguration.class */
    static class ReactiveTemplateEngineConfiguration {
        ReactiveTemplateEngineConfiguration() {
        }

        @ConditionalOnMissingBean({ISpringWebFluxTemplateEngine.class})
        @Bean
        SpringWebFluxTemplateEngine templateEngine(ThymeleafProperties properties, ObjectProvider<ITemplateResolver> templateResolvers, ObjectProvider<IDialect> dialects) {
            SpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();
            engine.setEnableSpringELCompiler(properties.isEnableSpringElCompiler());
            engine.setRenderHiddenMarkersBeforeCheckboxes(properties.isRenderHiddenMarkersBeforeCheckboxes());
            Stream<ITemplateResolver> streamOrderedStream = templateResolvers.orderedStream();
            engine.getClass();
            streamOrderedStream.forEach(engine::addTemplateResolver);
            Stream<IDialect> streamOrderedStream2 = dialects.orderedStream();
            engine.getClass();
            streamOrderedStream2.forEach(engine::addDialect);
            return engine;
        }
    }
}
