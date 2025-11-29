package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.elasticsearch.webclient")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ReactiveElasticsearchRestClientProperties.class */
public class ReactiveElasticsearchRestClientProperties {
    private DataSize maxInMemorySize;

    public DataSize getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(DataSize maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }
}
