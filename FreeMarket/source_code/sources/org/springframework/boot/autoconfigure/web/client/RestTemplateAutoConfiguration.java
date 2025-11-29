package org.springframework.boot.autoconfigure.web.client;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration(after = {HttpMessageConvertersAutoConfiguration.class})
@ConditionalOnClass({RestTemplate.class})
@Conditional({NotReactiveWebApplicationCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateAutoConfiguration.class */
public class RestTemplateAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public RestTemplateBuilderConfigurer restTemplateBuilderConfigurer(ObjectProvider<HttpMessageConverters> messageConverters, ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers, ObjectProvider<RestTemplateRequestCustomizer<?>> restTemplateRequestCustomizers) {
        RestTemplateBuilderConfigurer configurer = new RestTemplateBuilderConfigurer();
        configurer.setHttpMessageConverters(messageConverters.getIfUnique());
        configurer.setRestTemplateCustomizers((List) restTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        configurer.setRestTemplateRequestCustomizers((List) restTemplateRequestCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer) {
        RestTemplateBuilder builder = new RestTemplateBuilder(new RestTemplateCustomizer[0]);
        return restTemplateBuilderConfigurer.configure(builder);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateAutoConfiguration$NotReactiveWebApplicationCondition.class */
    static class NotReactiveWebApplicationCondition extends NoneNestedConditions {
        NotReactiveWebApplicationCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateAutoConfiguration$NotReactiveWebApplicationCondition$ReactiveWebApplication.class */
        private static class ReactiveWebApplication {
            private ReactiveWebApplication() {
            }
        }
    }
}
