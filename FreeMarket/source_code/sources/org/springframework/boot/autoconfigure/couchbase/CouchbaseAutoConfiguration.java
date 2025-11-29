package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.core.env.IoConfig;
import com.couchbase.client.core.env.SecurityConfig;
import com.couchbase.client.core.env.TimeoutConfig;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.json.JsonValueModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ResourceUtils;

@EnableConfigurationProperties({CouchbaseProperties.class})
@AutoConfiguration(after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({Cluster.class})
@ConditionalOnProperty({"spring.couchbase.connection-string"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration.class */
public class CouchbaseAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public ClusterEnvironment couchbaseClusterEnvironment(CouchbaseProperties properties, ObjectProvider<ClusterEnvironmentBuilderCustomizer> customizers) {
        ClusterEnvironment.Builder builder = initializeEnvironmentBuilder(properties);
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "disconnect")
    public Cluster couchbaseCluster(CouchbaseProperties properties, ClusterEnvironment couchbaseClusterEnvironment) {
        ClusterOptions options = ClusterOptions.clusterOptions(properties.getUsername(), properties.getPassword()).environment(couchbaseClusterEnvironment);
        return Cluster.connect(properties.getConnectionString(), options);
    }

    private ClusterEnvironment.Builder initializeEnvironmentBuilder(CouchbaseProperties properties) {
        ClusterEnvironment.Builder builder = ClusterEnvironment.builder();
        CouchbaseProperties.Timeouts timeouts = properties.getEnv().getTimeouts();
        builder.timeoutConfig(TimeoutConfig.kvTimeout(timeouts.getKeyValue()).analyticsTimeout(timeouts.getAnalytics()).kvDurableTimeout(timeouts.getKeyValueDurable()).queryTimeout(timeouts.getQuery()).viewTimeout(timeouts.getView()).searchTimeout(timeouts.getSearch()).managementTimeout(timeouts.getManagement()).connectTimeout(timeouts.getConnect()).disconnectTimeout(timeouts.getDisconnect()));
        CouchbaseProperties.Io io = properties.getEnv().getIo();
        builder.ioConfig(IoConfig.maxHttpConnections(io.getMaxEndpoints()).numKvConnections(io.getMinEndpoints()).idleHttpConnectionTimeout(io.getIdleHttpConnectionTimeout()));
        if (properties.getEnv().getSsl().getEnabled().booleanValue()) {
            builder.securityConfig(SecurityConfig.enableTls(true).trustManagerFactory(getTrustManagerFactory(properties.getEnv().getSsl())));
        }
        return builder;
    }

    private TrustManagerFactory getTrustManagerFactory(CouchbaseProperties.Ssl ssl) throws NoSuchAlgorithmException, KeyStoreException {
        String resource = ssl.getKeyStore();
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = loadKeyStore(resource, ssl.getKeyStorePassword());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load Couchbase key store '" + resource + "'", ex);
        }
    }

    private KeyStore loadKeyStore(String resource, String keyStorePassword) throws Exception {
        char[] charArray;
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        URL url = ResourceUtils.getURL(resource);
        InputStream stream = url.openStream();
        Throwable th = null;
        if (keyStorePassword != null) {
            try {
                try {
                    charArray = keyStorePassword.toCharArray();
                } finally {
                }
            } catch (Throwable th2) {
                if (stream != null) {
                    if (th != null) {
                        try {
                            stream.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        stream.close();
                    }
                }
                throw th2;
            }
        } else {
            charArray = null;
        }
        store.load(stream, charArray);
        if (stream != null) {
            if (0 != 0) {
                try {
                    stream.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                stream.close();
            }
        }
        return store;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$JacksonConfiguration.class */
    static class JacksonConfiguration {
        JacksonConfiguration() {
        }

        @Bean
        @ConditionalOnSingleCandidate(ObjectMapper.class)
        ClusterEnvironmentBuilderCustomizer jacksonClusterEnvironmentBuilderCustomizer(ObjectMapper objectMapper) {
            return new JacksonClusterEnvironmentBuilderCustomizer(objectMapper.copy().registerModule(new JsonValueModule()));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$JacksonClusterEnvironmentBuilderCustomizer.class */
    private static final class JacksonClusterEnvironmentBuilderCustomizer implements ClusterEnvironmentBuilderCustomizer, Ordered {
        private final ObjectMapper objectMapper;

        private JacksonClusterEnvironmentBuilderCustomizer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override // org.springframework.boot.autoconfigure.couchbase.ClusterEnvironmentBuilderCustomizer
        public void customize(ClusterEnvironment.Builder builder) {
            builder.jsonSerializer(JacksonJsonSerializer.create(this.objectMapper));
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }
    }
}
