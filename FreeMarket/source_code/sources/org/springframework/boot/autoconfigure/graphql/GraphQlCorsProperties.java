package org.springframework.boot.autoconfigure.graphql;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties(prefix = "spring.graphql.cors")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/GraphQlCorsProperties.class */
public class GraphQlCorsProperties {
    private Boolean allowCredentials;
    private List<String> allowedOrigins = new ArrayList();
    private List<String> allowedOriginPatterns = new ArrayList();
    private List<String> allowedMethods = new ArrayList();
    private List<String> allowedHeaders = new ArrayList();
    private List<String> exposedHeaders = new ArrayList();

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration maxAge = Duration.ofSeconds(CrossOrigin.DEFAULT_MAX_AGE);

    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOriginPatterns() {
        return this.allowedOriginPatterns;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public List<String> getAllowedMethods() {
        return this.allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    public Boolean getAllowCredentials() {
        return this.allowCredentials;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public Duration getMaxAge() {
        return this.maxAge;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }

    public CorsConfiguration toCorsConfiguration() {
        if (CollectionUtils.isEmpty(this.allowedOrigins) && CollectionUtils.isEmpty(this.allowedOriginPatterns)) {
            return null;
        }
        PropertyMapper map = PropertyMapper.get();
        CorsConfiguration config = new CorsConfiguration();
        PropertyMapper.Source sourceFrom = map.from(this::getAllowedOrigins);
        config.getClass();
        sourceFrom.to(config::setAllowedOrigins);
        PropertyMapper.Source sourceFrom2 = map.from(this::getAllowedOriginPatterns);
        config.getClass();
        sourceFrom2.to(config::setAllowedOriginPatterns);
        PropertyMapper.Source sourceWhenNot = map.from(this::getAllowedHeaders).whenNot((v0) -> {
            return CollectionUtils.isEmpty(v0);
        });
        config.getClass();
        sourceWhenNot.to(config::setAllowedHeaders);
        PropertyMapper.Source sourceWhenNot2 = map.from(this::getAllowedMethods).whenNot((v0) -> {
            return CollectionUtils.isEmpty(v0);
        });
        config.getClass();
        sourceWhenNot2.to(config::setAllowedMethods);
        PropertyMapper.Source sourceWhenNot3 = map.from(this::getExposedHeaders).whenNot((v0) -> {
            return CollectionUtils.isEmpty(v0);
        });
        config.getClass();
        sourceWhenNot3.to(config::setExposedHeaders);
        PropertyMapper.Source sourceAs = map.from(this::getMaxAge).whenNonNull().as((v0) -> {
            return v0.getSeconds();
        });
        config.getClass();
        sourceAs.to(config::setMaxAge);
        PropertyMapper.Source sourceWhenNonNull = map.from(this::getAllowCredentials).whenNonNull();
        config.getClass();
        sourceWhenNonNull.to(config::setAllowCredentials);
        return config;
    }
}
