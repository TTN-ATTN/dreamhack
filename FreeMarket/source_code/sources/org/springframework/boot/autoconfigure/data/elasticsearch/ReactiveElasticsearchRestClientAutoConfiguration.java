package org.springframework.boot.autoconfigure.data.elasticsearch;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@EnableConfigurationProperties({ElasticsearchProperties.class, ReactiveElasticsearchRestClientProperties.class, DeprecatedReactiveElasticsearchRestClientProperties.class})
@AutoConfiguration
@ConditionalOnClass({ReactiveRestClients.class, WebClient.class, HttpClient.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ReactiveElasticsearchRestClientAutoConfiguration.class */
public class ReactiveElasticsearchRestClientAutoConfiguration {
    private final ConsolidatedProperties properties;

    ReactiveElasticsearchRestClientAutoConfiguration(ElasticsearchProperties properties, ReactiveElasticsearchRestClientProperties restClientProperties, DeprecatedReactiveElasticsearchRestClientProperties reactiveProperties) {
        this.properties = new ConsolidatedProperties(properties, restClientProperties, reactiveProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder().connectedTo((String[]) this.properties.getEndpoints().toArray(new String[0]));
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source sourceWhenTrue = map.from((PropertyMapper) Boolean.valueOf(this.properties.isUseSsl())).whenTrue();
        builder.getClass();
        sourceWhenTrue.toCall(builder::usingSsl);
        map.from((PropertyMapper) this.properties.getCredentials()).to(credentials -> {
            builder.withBasicAuth(credentials.getUsername(), credentials.getPassword());
        });
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) this.properties.getConnectionTimeout());
        builder.getClass();
        sourceFrom.to(builder::withConnectTimeout);
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) this.properties.getSocketTimeout());
        builder.getClass();
        sourceFrom2.to(builder::withSocketTimeout);
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) this.properties.getPathPrefix());
        builder.getClass();
        sourceFrom3.to(builder::withPathPrefix);
        configureExchangeStrategies(map, builder);
        return builder.build();
    }

    private void configureExchangeStrategies(PropertyMapper map, ClientConfiguration.TerminalClientConfigurationBuilder builder) {
        map.from((PropertyMapper) this.properties.getMaxInMemorySize()).asInt((v0) -> {
            return v0.toBytes();
        }).to(maxInMemorySize -> {
            builder.withClientConfigurer(ReactiveRestClients.WebClientConfigurationCallback.from(webClient -> {
                ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(maxInMemorySize.intValue());
                }).build();
                return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
            }));
        });
    }

    @ConditionalOnMissingBean
    @Bean
    public ReactiveElasticsearchClient reactiveElasticsearchClient(ClientConfiguration clientConfiguration) {
        return ReactiveRestClients.create(clientConfiguration);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ReactiveElasticsearchRestClientAutoConfiguration$ConsolidatedProperties.class */
    private static final class ConsolidatedProperties {
        private final ElasticsearchProperties properties;
        private final ReactiveElasticsearchRestClientProperties restClientProperties;
        private final DeprecatedReactiveElasticsearchRestClientProperties deprecatedProperties;
        private final List<URI> uris;

        private ConsolidatedProperties(ElasticsearchProperties properties, ReactiveElasticsearchRestClientProperties restClientProperties, DeprecatedReactiveElasticsearchRestClientProperties deprecatedreactiveProperties) {
            this.properties = properties;
            this.restClientProperties = restClientProperties;
            this.deprecatedProperties = deprecatedreactiveProperties;
            this.uris = (List) properties.getUris().stream().map(s -> {
                return s.startsWith("http") ? s : "http://" + s;
            }).map(URI::create).collect(Collectors.toList());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<String> getEndpoints() {
            if (this.deprecatedProperties.isCustomized()) {
                return this.deprecatedProperties.getEndpoints();
            }
            return (List) this.uris.stream().map(this::getEndpoint).collect(Collectors.toList());
        }

        private String getEndpoint(URI uri) {
            return uri.getHost() + ":" + uri.getPort();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Credentials getCredentials() {
            if (this.deprecatedProperties.isCustomized()) {
                return Credentials.from(this.deprecatedProperties);
            }
            Credentials propertyCredentials = Credentials.from(this.properties);
            Credentials uriCredentials = Credentials.from(this.uris);
            if (uriCredentials == null) {
                return propertyCredentials;
            }
            Assert.isTrue(propertyCredentials == null || uriCredentials.equals(propertyCredentials), "Credentials from URI user info do not match those from spring.elasticsearch.username and spring.elasticsearch.password");
            return uriCredentials;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Duration getConnectionTimeout() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getConnectionTimeout() : this.properties.getConnectionTimeout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Duration getSocketTimeout() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getSocketTimeout() : this.properties.getSocketTimeout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isUseSsl() {
            if (this.deprecatedProperties.isCustomized()) {
                return this.deprecatedProperties.isUseSsl();
            }
            Set<String> schemes = (Set) this.uris.stream().map((v0) -> {
                return v0.getScheme();
            }).collect(Collectors.toSet());
            Assert.isTrue(schemes.size() == 1, "Configured Elasticsearch URIs have varying schemes");
            return schemes.iterator().next().equals("https");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public DataSize getMaxInMemorySize() {
            return this.deprecatedProperties.isCustomized() ? this.deprecatedProperties.getMaxInMemorySize() : this.restClientProperties.getMaxInMemorySize();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getPathPrefix() {
            if (this.deprecatedProperties.isCustomized()) {
                return null;
            }
            return this.properties.getPathPrefix();
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ReactiveElasticsearchRestClientAutoConfiguration$ConsolidatedProperties$Credentials.class */
        private static final class Credentials {
            private final String username;
            private final String password;

            private Credentials(String username, String password) {
                this.username = username;
                this.password = password;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public String getUsername() {
                return this.username;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public String getPassword() {
                return this.password;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static Credentials from(List<URI> uris) {
                Set<String> userInfos = (Set) uris.stream().map((v0) -> {
                    return v0.getUserInfo();
                }).collect(Collectors.toSet());
                Assert.isTrue(userInfos.size() == 1, "Configured Elasticsearch URIs have varying user infos");
                String userInfo = userInfos.iterator().next();
                if (userInfo == null) {
                    return null;
                }
                String[] parts = userInfo.split(":");
                String username = parts[0];
                String password = parts.length != 2 ? "" : parts[1];
                return new Credentials(username, password);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static Credentials from(ElasticsearchProperties properties) {
                return getCredentials(properties.getUsername(), properties.getPassword());
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static Credentials from(DeprecatedReactiveElasticsearchRestClientProperties properties) {
                return getCredentials(properties.getUsername(), properties.getPassword());
            }

            private static Credentials getCredentials(String username, String password) {
                if (username == null && password == null) {
                    return null;
                }
                return new Credentials(username, password);
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || getClass() != obj.getClass()) {
                    return false;
                }
                Credentials other = (Credentials) obj;
                return ObjectUtils.nullSafeEquals(this.username, other.username) && ObjectUtils.nullSafeEquals(this.password, other.password);
            }

            public int hashCode() {
                int result = (31 * 1) + ObjectUtils.nullSafeHashCode(this.username);
                return (31 * result) + ObjectUtils.nullSafeHashCode(this.password);
            }
        }
    }
}
