package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@AutoConfiguration(after = {ElasticsearchRestClientAutoConfiguration.class, ReactiveElasticsearchRestClientAutoConfiguration.class})
@ConditionalOnClass({ElasticsearchRestTemplate.class})
@Import({ElasticsearchDataConfiguration.BaseConfiguration.class, ElasticsearchDataConfiguration.RestClientConfiguration.class, ElasticsearchDataConfiguration.ReactiveRestClientConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataAutoConfiguration.class */
public class ElasticsearchDataAutoConfiguration {
}
