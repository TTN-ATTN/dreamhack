package org.springframework.boot.autoconfigure.elasticsearch;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.sniff.SnifferBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations.class */
class ElasticsearchRestClientConfigurations {
    ElasticsearchRestClientConfigurations() {
    }

    @ConditionalOnMissingBean({RestClientBuilder.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientBuilderConfiguration.class */
    static class RestClientBuilderConfiguration {
        private final ConsolidatedProperties properties;

        RestClientBuilderConfiguration(ElasticsearchProperties properties, DeprecatedElasticsearchRestClientProperties deprecatedProperties) {
            this.properties = new ConsolidatedProperties(properties, deprecatedProperties);
        }

        @Bean
        RestClientBuilderCustomizer defaultRestClientBuilderCustomizer() {
            return new DefaultRestClientBuilderCustomizer(this.properties);
        }

        @Bean
        RestClientBuilder elasticsearchRestClientBuilder(ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
            HttpHost[] hosts = (HttpHost[]) this.properties.getUris().stream().map(this::createHttpHost).toArray(x$0 -> {
                return new HttpHost[x$0];
            });
            RestClientBuilder builder = RestClient.builder(hosts);
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                builderCustomizers.orderedStream().forEach(customizer -> {
                    customizer.customize(httpClientBuilder);
                });
                return httpClientBuilder;
            });
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                builderCustomizers.orderedStream().forEach(customizer -> {
                    customizer.customize(requestConfigBuilder);
                });
                return requestConfigBuilder;
            });
            if (this.properties.getPathPrefix() != null) {
                builder.setPathPrefix(this.properties.properties.getPathPrefix());
            }
            builderCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(builder);
            });
            return builder;
        }

        private HttpHost createHttpHost(String uri) {
            try {
                return createHttpHost(URI.create(uri));
            } catch (IllegalArgumentException e) {
                return HttpHost.create(uri);
            }
        }

        private HttpHost createHttpHost(URI uri) {
            if (!StringUtils.hasLength(uri.getUserInfo())) {
                return HttpHost.create(uri.toString());
            }
            try {
                return HttpHost.create(new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString());
            } catch (URISyntaxException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @ConditionalOnMissingBean({RestHighLevelClient.class, RestClient.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({RestHighLevelClient.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestHighLevelClientConfiguration.class */
    static class RestHighLevelClientConfiguration {
        RestHighLevelClientConfiguration() {
        }

        @Bean
        RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
            return new RestHighLevelClient(restClientBuilder);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({RestHighLevelClient.class})
    @ConditionalOnSingleCandidate(RestHighLevelClient.class)
    @ConditionalOnMissingBean({RestClient.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientFromRestHighLevelClientConfiguration.class */
    static class RestClientFromRestHighLevelClientConfiguration {
        RestClientFromRestHighLevelClientConfiguration() {
        }

        @Bean
        RestClient elasticsearchRestClient(RestHighLevelClient restHighLevelClient) {
            return restHighLevelClient.getLowLevelClient();
        }
    }

    @ConditionalOnMissingBean({RestClient.class})
    @ConditionalOnMissingClass({"org.elasticsearch.client.RestHighLevelClient"})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientConfiguration.class */
    static class RestClientConfiguration {
        RestClientConfiguration() {
        }

        @Bean
        RestClient elasticsearchRestClient(RestClientBuilder restClientBuilder) {
            return restClientBuilder.build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Sniffer.class})
    @ConditionalOnSingleCandidate(RestClient.class)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestClientSnifferConfiguration.class */
    static class RestClientSnifferConfiguration {
        RestClientSnifferConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        Sniffer elasticsearchSniffer(RestClient client, ElasticsearchRestClientProperties properties, DeprecatedElasticsearchRestClientProperties deprecatedProperties) {
            Duration delayAfterFailure;
            SnifferBuilder builder = Sniffer.builder(client);
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Duration interval = deprecatedProperties.isCustomized() ? deprecatedProperties.getSniffer().getInterval() : properties.getSniffer().getInterval();
            PropertyMapper.Source<Integer> sourceAsInt = map.from((PropertyMapper) interval).asInt((v0) -> {
                return v0.toMillis();
            });
            builder.getClass();
            sourceAsInt.to((v1) -> {
                r1.setSniffIntervalMillis(v1);
            });
            if (deprecatedProperties.isCustomized()) {
                delayAfterFailure = deprecatedProperties.getSniffer().getDelayAfterFailure();
            } else {
                delayAfterFailure = properties.getSniffer().getDelayAfterFailure();
            }
            Duration delayAfterFailure2 = delayAfterFailure;
            PropertyMapper.Source<Integer> sourceAsInt2 = map.from((PropertyMapper) delayAfterFailure2).asInt((v0) -> {
                return v0.toMillis();
            });
            builder.getClass();
            sourceAsInt2.to((v1) -> {
                r1.setSniffAfterFailureDelayMillis(v1);
            });
            return builder.build();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$DefaultRestClientBuilderCustomizer.class */
    static class DefaultRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private static final PropertyMapper map = PropertyMapper.get();
        private final ConsolidatedProperties properties;

        DefaultRestClientBuilderCustomizer(ConsolidatedProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(RestClientBuilder builder) {
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(HttpAsyncClientBuilder builder) {
            builder.setDefaultCredentialsProvider(new PropertiesCredentialsProvider(this.properties));
        }

        @Override // org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
        public void customize(RequestConfig.Builder builder) {
            PropertyMapper propertyMapper = map;
            ConsolidatedProperties consolidatedProperties = this.properties;
            consolidatedProperties.getClass();
            PropertyMapper.Source<Integer> sourceAsInt = propertyMapper.from(() -> {
                return consolidatedProperties.getConnectionTimeout();
            }).whenNonNull().asInt((v0) -> {
                return v0.toMillis();
            });
            builder.getClass();
            sourceAsInt.to((v1) -> {
                r1.setConnectTimeout(v1);
            });
            PropertyMapper propertyMapper2 = map;
            ConsolidatedProperties consolidatedProperties2 = this.properties;
            consolidatedProperties2.getClass();
            PropertyMapper.Source<Integer> sourceAsInt2 = propertyMapper2.from(() -> {
                return consolidatedProperties2.getSocketTimeout();
            }).whenNonNull().asInt((v0) -> {
                return v0.toMillis();
            });
            builder.getClass();
            sourceAsInt2.to((v1) -> {
                r1.setSocketTimeout(v1);
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$PropertiesCredentialsProvider.class */
    private static class PropertiesCredentialsProvider extends BasicCredentialsProvider {
        PropertiesCredentialsProvider(ConsolidatedProperties properties) {
            if (StringUtils.hasText(properties.getUsername())) {
                Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword());
                setCredentials(AuthScope.ANY, credentials);
            }
            properties.getUris().stream().map(this::toUri).filter(this::hasUserInfo).forEach(this::addUserInfoCredentials);
        }

        private URI toUri(String uri) {
            try {
                return URI.create(uri);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private boolean hasUserInfo(URI uri) {
            return uri != null && StringUtils.hasLength(uri.getUserInfo());
        }

        private void addUserInfoCredentials(URI uri) {
            AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
            Credentials credentials = createUserInfoCredentials(uri.getUserInfo());
            setCredentials(authScope, credentials);
        }

        private Credentials createUserInfoCredentials(String userInfo) {
            int delimiter = userInfo.indexOf(":");
            if (delimiter == -1) {
                return new UsernamePasswordCredentials(userInfo, null);
            }
            String username = userInfo.substring(0, delimiter);
            String password = userInfo.substring(delimiter + 1);
            return new UsernamePasswordCredentials(username, password);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$ConsolidatedProperties.class */
    private static final class ConsolidatedProperties {
        private final ElasticsearchProperties properties;
        private final DeprecatedElasticsearchRestClientProperties deprecatedProperties;

        private ConsolidatedProperties(ElasticsearchProperties properties, DeprecatedElasticsearchRestClientProperties deprecatedProperties) {
            this.properties = properties;
            this.deprecatedProperties = deprecatedProperties;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<String> getUris() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getUris() : this.properties.getUris();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getUsername() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getUsername() : this.properties.getUsername();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getPassword() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getPassword() : this.properties.getPassword();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Duration getConnectionTimeout() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getConnectionTimeout() : this.properties.getConnectionTimeout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Duration getSocketTimeout() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getReadTimeout() : this.properties.getSocketTimeout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getPathPrefix() {
            if (this.deprecatedProperties.isCustomized()) {
                return null;
            }
            return this.properties.getPathPrefix();
        }
    }
}
