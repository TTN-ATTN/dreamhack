package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.DriverOption;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;

@EnableConfigurationProperties({CassandraProperties.class})
@AutoConfiguration
@ConditionalOnClass({CqlSession.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration.class */
public class CassandraAutoConfiguration {
    private static final Config SPRING_BOOT_DEFAULTS;

    static {
        CassandraDriverOptions options = new CassandraDriverOptions();
        options.add((DriverOption) DefaultDriverOption.CONTACT_POINTS, (List<String>) Collections.singletonList("127.0.0.1:9042"));
        options.add((DriverOption) DefaultDriverOption.PROTOCOL_COMPRESSION, "none");
        options.add((DriverOption) DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT, (int) Duration.ofSeconds(5L).toMillis());
        SPRING_BOOT_DEFAULTS = options.build();
    }

    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public CqlSession cassandraSession(CqlSessionBuilder cqlSessionBuilder) {
        return (CqlSession) cqlSessionBuilder.build();
    }

    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public CqlSessionBuilder cassandraSessionBuilder(CassandraProperties properties, DriverConfigLoader driverConfigLoader, ObjectProvider<CqlSessionBuilderCustomizer> builderCustomizers) {
        CqlSessionBuilder builder = (CqlSessionBuilder) CqlSession.builder().withConfigLoader(driverConfigLoader);
        configureAuthentication(properties, builder);
        configureSsl(properties, builder);
        builder.withKeyspace(properties.getKeyspaceName());
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    private void configureAuthentication(CassandraProperties properties, CqlSessionBuilder builder) {
        if (properties.getUsername() != null) {
            builder.withAuthCredentials(properties.getUsername(), properties.getPassword());
        }
    }

    private void configureSsl(CassandraProperties properties, CqlSessionBuilder builder) {
        if (properties.isSsl()) {
            try {
                builder.withSslContext(SSLContext.getDefault());
            } catch (NoSuchAlgorithmException ex) {
                throw new IllegalStateException("Could not setup SSL default context for Cassandra", ex);
            }
        }
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "")
    public DriverConfigLoader cassandraDriverConfigLoader(CassandraProperties properties, ObjectProvider<DriverConfigLoaderBuilderCustomizer> builderCustomizers) {
        DefaultProgrammaticDriverConfigLoaderBuilder defaultProgrammaticDriverConfigLoaderBuilder = new DefaultProgrammaticDriverConfigLoaderBuilder(() -> {
            return cassandraConfiguration(properties);
        }, "datastax-java-driver");
        builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(defaultProgrammaticDriverConfigLoaderBuilder);
        });
        return defaultProgrammaticDriverConfigLoaderBuilder.build();
    }

    private Config cassandraConfiguration(CassandraProperties properties) {
        ConfigFactory.invalidateCaches();
        Config config = ConfigFactory.defaultOverrides().withFallback(mapConfig(properties));
        if (properties.getConfig() != null) {
            config = config.withFallback(loadConfig(properties.getConfig()));
        }
        return config.withFallback(SPRING_BOOT_DEFAULTS).withFallback(ConfigFactory.defaultReferenceUnresolved()).resolve();
    }

    private Config loadConfig(Resource resource) {
        try {
            return ConfigFactory.parseURL(resource.getURL());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load cassandra configuration from " + resource, ex);
        }
    }

    private Config mapConfig(CassandraProperties properties) {
        CassandraDriverOptions options = new CassandraDriverOptions();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from((PropertyMapper) properties.getSessionName()).whenHasText().to(sessionName -> {
            options.add((DriverOption) DefaultDriverOption.SESSION_NAME, sessionName);
        });
        properties.getClass();
        map.from(properties::getUsername).to(username -> {
            options.add((DriverOption) DefaultDriverOption.AUTH_PROVIDER_USER_NAME, username).add((DriverOption) DefaultDriverOption.AUTH_PROVIDER_PASSWORD, properties.getPassword());
        });
        properties.getClass();
        map.from(properties::getCompression).to(compression -> {
            options.add((DriverOption) DefaultDriverOption.PROTOCOL_COMPRESSION, (Enum<?>) compression);
        });
        mapConnectionOptions(properties, options);
        mapPoolingOptions(properties, options);
        mapRequestOptions(properties, options);
        mapControlConnectionOptions(properties, options);
        map.from((PropertyMapper) mapContactPoints(properties)).to(contactPoints -> {
            options.add((DriverOption) DefaultDriverOption.CONTACT_POINTS, (List<String>) contactPoints);
        });
        map.from((PropertyMapper) properties.getLocalDatacenter()).whenHasText().to(localDatacenter -> {
            options.add((DriverOption) DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDatacenter);
        });
        return options.build();
    }

    private void mapConnectionOptions(CassandraProperties properties, CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Connection connectionProperties = properties.getConnection();
        connectionProperties.getClass();
        map.from(connectionProperties::getConnectTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(connectTimeout -> {
            options.add((DriverOption) DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, connectTimeout.intValue());
        });
        connectionProperties.getClass();
        map.from(connectionProperties::getInitQueryTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(initQueryTimeout -> {
            options.add((DriverOption) DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, initQueryTimeout.intValue());
        });
    }

    private void mapPoolingOptions(CassandraProperties properties, CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Pool poolProperties = properties.getPool();
        poolProperties.getClass();
        map.from(poolProperties::getIdleTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(idleTimeout -> {
            options.add((DriverOption) DefaultDriverOption.HEARTBEAT_TIMEOUT, idleTimeout.intValue());
        });
        poolProperties.getClass();
        map.from(poolProperties::getHeartbeatInterval).asInt((v0) -> {
            return v0.toMillis();
        }).to(heartBeatInterval -> {
            options.add((DriverOption) DefaultDriverOption.HEARTBEAT_INTERVAL, heartBeatInterval.intValue());
        });
    }

    private void mapRequestOptions(CassandraProperties properties, CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Request requestProperties = properties.getRequest();
        requestProperties.getClass();
        map.from(requestProperties::getTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(timeout -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_TIMEOUT, timeout.intValue());
        });
        requestProperties.getClass();
        map.from(requestProperties::getConsistency).to(consistency -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_CONSISTENCY, (Enum<?>) consistency);
        });
        requestProperties.getClass();
        map.from(requestProperties::getSerialConsistency).to(serialConsistency -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, (Enum<?>) serialConsistency);
        });
        requestProperties.getClass();
        map.from(requestProperties::getPageSize).to(pageSize -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_PAGE_SIZE, pageSize.intValue());
        });
        CassandraProperties.Throttler throttlerProperties = requestProperties.getThrottler();
        throttlerProperties.getClass();
        map.from(throttlerProperties::getType).as((v0) -> {
            return v0.type();
        }).to(type -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_CLASS, type);
        });
        throttlerProperties.getClass();
        map.from(throttlerProperties::getMaxQueueSize).to(maxQueueSize -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_QUEUE_SIZE, maxQueueSize.intValue());
        });
        throttlerProperties.getClass();
        map.from(throttlerProperties::getMaxConcurrentRequests).to(maxConcurrentRequests -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_CONCURRENT_REQUESTS, maxConcurrentRequests.intValue());
        });
        throttlerProperties.getClass();
        map.from(throttlerProperties::getMaxRequestsPerSecond).to(maxRequestsPerSecond -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_MAX_REQUESTS_PER_SECOND, maxRequestsPerSecond.intValue());
        });
        throttlerProperties.getClass();
        map.from(throttlerProperties::getDrainInterval).asInt((v0) -> {
            return v0.toMillis();
        }).to(drainInterval -> {
            options.add((DriverOption) DefaultDriverOption.REQUEST_THROTTLER_DRAIN_INTERVAL, drainInterval.intValue());
        });
    }

    private void mapControlConnectionOptions(CassandraProperties properties, CassandraDriverOptions options) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        CassandraProperties.Controlconnection controlProperties = properties.getControlconnection();
        controlProperties.getClass();
        map.from(controlProperties::getTimeout).asInt((v0) -> {
            return v0.toMillis();
        }).to(timeout -> {
            options.add((DriverOption) DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT, timeout.intValue());
        });
    }

    private List<String> mapContactPoints(CassandraProperties properties) {
        if (properties.getContactPoints() != null) {
            return (List) properties.getContactPoints().stream().map(candidate -> {
                return formatContactPoint(candidate, properties.getPort());
            }).collect(Collectors.toList());
        }
        return null;
    }

    private String formatContactPoint(String candidate, int port) {
        int i = candidate.lastIndexOf(58);
        if (i == -1 || !isPort(() -> {
            return candidate.substring(i + 1);
        })) {
            return String.format("%s:%s", candidate, Integer.valueOf(port));
        }
        return candidate;
    }

    private boolean isPort(Supplier<String> value) throws NumberFormatException {
        try {
            int i = Integer.parseInt(value.get());
            return i > 0 && i < 65535;
        } catch (Exception e) {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration$CassandraDriverOptions.class */
    private static class CassandraDriverOptions {
        private final Map<String, String> options;

        private CassandraDriverOptions() {
            this.options = new LinkedHashMap();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, String value) {
            String key = createKeyFor(option);
            this.options.put(key, value);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, int value) {
            return add(option, String.valueOf(value));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, Enum<?> value) {
            return add(option, value.name());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CassandraDriverOptions add(DriverOption option, List<String> values) {
            for (int i = 0; i < values.size(); i++) {
                this.options.put(String.format("%s.%s", createKeyFor(option), Integer.valueOf(i)), values.get(i));
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Config build() {
            return ConfigFactory.parseMap(this.options, "Environment");
        }

        private static String createKeyFor(DriverOption option) {
            return String.format("%s.%s", "datastax-java-driver", option.getPath());
        }
    }
}
