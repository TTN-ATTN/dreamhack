package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.mustache.MustacheProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.reactive.result.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mustache/MustacheReactiveWebConfiguration.class */
class MustacheReactiveWebConfiguration {
    MustacheReactiveWebConfiguration() {
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.mustache", name = {"enabled"}, matchIfMissing = true)
    @Bean
    MustacheViewResolver mustacheViewResolver(Mustache.Compiler mustacheCompiler, MustacheProperties mustache) {
        MustacheViewResolver resolver = new MustacheViewResolver(mustacheCompiler);
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mustache.getClass();
        PropertyMapper.Source sourceFrom = map.from(mustache::getPrefix);
        resolver.getClass();
        sourceFrom.to(resolver::setPrefix);
        mustache.getClass();
        PropertyMapper.Source sourceFrom2 = map.from(mustache::getSuffix);
        resolver.getClass();
        sourceFrom2.to(resolver::setSuffix);
        mustache.getClass();
        PropertyMapper.Source sourceFrom3 = map.from(mustache::getViewNames);
        resolver.getClass();
        sourceFrom3.to(resolver::setViewNames);
        mustache.getClass();
        PropertyMapper.Source sourceFrom4 = map.from(mustache::getRequestContextAttribute);
        resolver.getClass();
        sourceFrom4.to(resolver::setRequestContextAttribute);
        mustache.getClass();
        PropertyMapper.Source sourceFrom5 = map.from(mustache::getCharsetName);
        resolver.getClass();
        sourceFrom5.to(resolver::setCharset);
        MustacheProperties.Reactive reactive = mustache.getReactive();
        reactive.getClass();
        PropertyMapper.Source sourceFrom6 = map.from(reactive::getMediaTypes);
        resolver.getClass();
        sourceFrom6.to(resolver::setSupportedMediaTypes);
        resolver.setOrder(2147483637);
        return resolver;
    }
}
