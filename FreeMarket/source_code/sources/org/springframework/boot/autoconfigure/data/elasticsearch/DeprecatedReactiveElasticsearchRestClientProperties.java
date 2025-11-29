package org.springframework.boot.autoconfigure.data.elasticsearch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.data.elasticsearch.client.reactive")
@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/DeprecatedReactiveElasticsearchRestClientProperties.class */
class DeprecatedReactiveElasticsearchRestClientProperties {
    private String username;
    private String password;
    private Duration connectionTimeout;
    private Duration socketTimeout;
    private DataSize maxInMemorySize;
    private List<String> endpoints = new ArrayList(Collections.singletonList("localhost:9200"));
    private boolean useSsl = false;
    private boolean customized = false;

    DeprecatedReactiveElasticsearchRestClientProperties() {
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.uris")
    public List<String> getEndpoints() {
        return this.endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.customized = true;
        this.endpoints = endpoints;
    }

    @DeprecatedConfigurationProperty(reason = "Use of SSL should be indicated through an https URI scheme")
    public boolean isUseSsl() {
        return this.useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.customized = true;
        this.useSsl = useSsl;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.username")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.customized = true;
        this.username = username;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.password")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.customized = true;
        this.password = password;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.connection-timeout")
    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.customized = true;
        this.connectionTimeout = connectionTimeout;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.socket-timeout")
    public Duration getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
        this.customized = true;
        this.socketTimeout = socketTimeout;
    }

    @DeprecatedConfigurationProperty(replacement = "spring.elasticsearch.webclient.max-in-memory-size")
    public DataSize getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(DataSize maxInMemorySize) {
        this.customized = true;
        this.maxInMemorySize = maxInMemorySize;
    }

    boolean isCustomized() {
        return this.customized;
    }
}
