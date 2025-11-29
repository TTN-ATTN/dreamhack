package org.springframework.boot.autoconfigure.elasticsearch;

import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({ElasticsearchProperties.class, ElasticsearchRestClientProperties.class, DeprecatedElasticsearchRestClientProperties.class})
@AutoConfiguration
@ConditionalOnClass({RestClientBuilder.class})
@Import({ElasticsearchRestClientConfigurations.RestClientBuilderConfiguration.class, ElasticsearchRestClientConfigurations.RestHighLevelClientConfiguration.class, ElasticsearchRestClientConfigurations.RestClientFromRestHighLevelClientConfiguration.class, ElasticsearchRestClientConfigurations.RestClientConfiguration.class, ElasticsearchRestClientConfigurations.RestClientSnifferConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientAutoConfiguration.class */
public class ElasticsearchRestClientAutoConfiguration {
}
