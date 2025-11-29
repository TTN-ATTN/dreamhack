package org.springframework.boot.autoconfigure.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@EnableConfigurationProperties({GsonProperties.class})
@AutoConfiguration
@ConditionalOnClass({Gson.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration.class */
public class GsonAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) {
        GsonBuilder builder = new GsonBuilder();
        customizers.forEach(c -> {
            c.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean
    @Bean
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
    }

    @Bean
    public StandardGsonBuilderCustomizer standardGsonBuilderCustomizer(GsonProperties gsonProperties) {
        return new StandardGsonBuilderCustomizer(gsonProperties);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration$StandardGsonBuilderCustomizer.class */
    static final class StandardGsonBuilderCustomizer implements GsonBuilderCustomizer, Ordered {
        private final GsonProperties properties;

        StandardGsonBuilderCustomizer(GsonProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }

        @Override // org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer
        public void customize(GsonBuilder builder) {
            GsonProperties properties = this.properties;
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            properties.getClass();
            PropertyMapper.Source sourceFrom = map.from(properties::getGenerateNonExecutableJson);
            builder.getClass();
            sourceFrom.toCall(builder::generateNonExecutableJson);
            properties.getClass();
            PropertyMapper.Source sourceFrom2 = map.from(properties::getExcludeFieldsWithoutExposeAnnotation);
            builder.getClass();
            sourceFrom2.toCall(builder::excludeFieldsWithoutExposeAnnotation);
            properties.getClass();
            PropertyMapper.Source sourceWhenTrue = map.from(properties::getSerializeNulls).whenTrue();
            builder.getClass();
            sourceWhenTrue.toCall(builder::serializeNulls);
            properties.getClass();
            PropertyMapper.Source sourceFrom3 = map.from(properties::getEnableComplexMapKeySerialization);
            builder.getClass();
            sourceFrom3.toCall(builder::enableComplexMapKeySerialization);
            properties.getClass();
            PropertyMapper.Source sourceFrom4 = map.from(properties::getDisableInnerClassSerialization);
            builder.getClass();
            sourceFrom4.toCall(builder::disableInnerClassSerialization);
            properties.getClass();
            PropertyMapper.Source sourceFrom5 = map.from(properties::getLongSerializationPolicy);
            builder.getClass();
            sourceFrom5.to(builder::setLongSerializationPolicy);
            properties.getClass();
            PropertyMapper.Source sourceFrom6 = map.from(properties::getFieldNamingPolicy);
            builder.getClass();
            sourceFrom6.to(builder::setFieldNamingPolicy);
            properties.getClass();
            PropertyMapper.Source sourceFrom7 = map.from(properties::getPrettyPrinting);
            builder.getClass();
            sourceFrom7.toCall(builder::setPrettyPrinting);
            properties.getClass();
            PropertyMapper.Source sourceFrom8 = map.from(properties::getLenient);
            builder.getClass();
            sourceFrom8.toCall(builder::setLenient);
            properties.getClass();
            PropertyMapper.Source sourceFrom9 = map.from(properties::getDisableHtmlEscaping);
            builder.getClass();
            sourceFrom9.toCall(builder::disableHtmlEscaping);
            properties.getClass();
            PropertyMapper.Source sourceFrom10 = map.from(properties::getDateFormat);
            builder.getClass();
            sourceFrom10.to(builder::setDateFormat);
        }
    }
}
