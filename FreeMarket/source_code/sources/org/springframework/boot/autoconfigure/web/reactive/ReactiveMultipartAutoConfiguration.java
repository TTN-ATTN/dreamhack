package org.springframework.boot.autoconfigure.web.reactive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableConfigurationProperties({ReactiveMultipartProperties.class})
@AutoConfiguration
@ConditionalOnClass({DefaultPartHttpMessageReader.class, WebFluxConfigurer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveMultipartAutoConfiguration.class */
public class ReactiveMultipartAutoConfiguration {
    @Bean
    @Order(0)
    CodecCustomizer defaultPartHttpMessageReaderCustomizer(ReactiveMultipartProperties multipartProperties) {
        return configurer -> {
            configurer.defaultCodecs().configureDefaultCodec(codec -> {
                if (codec instanceof DefaultPartHttpMessageReader) {
                    DefaultPartHttpMessageReader defaultPartHttpMessageReader = (DefaultPartHttpMessageReader) codec;
                    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                    multipartProperties.getClass();
                    PropertyMapper.Source<Integer> sourceAsInt = map.from(multipartProperties::getMaxInMemorySize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    defaultPartHttpMessageReader.getClass();
                    sourceAsInt.to((v1) -> {
                        r1.setMaxInMemorySize(v1);
                    });
                    multipartProperties.getClass();
                    PropertyMapper.Source<Integer> sourceAsInt2 = map.from(multipartProperties::getMaxHeadersSize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    defaultPartHttpMessageReader.getClass();
                    sourceAsInt2.to((v1) -> {
                        r1.setMaxHeadersSize(v1);
                    });
                    multipartProperties.getClass();
                    PropertyMapper.Source<Integer> sourceAsInt3 = map.from(multipartProperties::getMaxDiskUsagePerPart).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    defaultPartHttpMessageReader.getClass();
                    sourceAsInt3.to((v1) -> {
                        r1.setMaxDiskUsagePerPart(v1);
                    });
                    multipartProperties.getClass();
                    PropertyMapper.Source sourceFrom = map.from(multipartProperties::getMaxParts);
                    defaultPartHttpMessageReader.getClass();
                    sourceFrom.to((v1) -> {
                        r1.setMaxParts(v1);
                    });
                    multipartProperties.getClass();
                    PropertyMapper.Source sourceFrom2 = map.from(multipartProperties::getStreaming);
                    defaultPartHttpMessageReader.getClass();
                    sourceFrom2.to((v1) -> {
                        r1.setStreaming(v1);
                    });
                    multipartProperties.getClass();
                    map.from(multipartProperties::getFileStorageDirectory).as(x$0 -> {
                        return Paths.get(x$0, new String[0]);
                    }).to(dir -> {
                        configureFileStorageDirectory(defaultPartHttpMessageReader, dir);
                    });
                    multipartProperties.getClass();
                    PropertyMapper.Source sourceFrom3 = map.from(multipartProperties::getHeadersCharset);
                    defaultPartHttpMessageReader.getClass();
                    sourceFrom3.to(defaultPartHttpMessageReader::setHeadersCharset);
                }
            });
        };
    }

    private void configureFileStorageDirectory(DefaultPartHttpMessageReader defaultPartHttpMessageReader, Path fileStorageDirectory) {
        try {
            defaultPartHttpMessageReader.setFileStorageDirectory(fileStorageDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to configure multipart file storage directory", ex);
        }
    }
}
