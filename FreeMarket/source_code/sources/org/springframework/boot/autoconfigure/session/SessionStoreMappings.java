package org.springframework.boot.autoconfigure.session;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.boot.WebApplicationType;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionStoreMappings.class */
final class SessionStoreMappings {
    private static final Map<StoreType, Configurations> MAPPINGS;

    static {
        Map<StoreType, Configurations> mappings = new EnumMap<>(StoreType.class);
        mappings.put(StoreType.REDIS, new Configurations(RedisSessionConfiguration.class, RedisReactiveSessionConfiguration.class));
        mappings.put(StoreType.MONGODB, new Configurations(MongoSessionConfiguration.class, MongoReactiveSessionConfiguration.class));
        mappings.put(StoreType.JDBC, new Configurations(JdbcSessionConfiguration.class, null));
        mappings.put(StoreType.HAZELCAST, new Configurations(HazelcastSessionConfiguration.class, null));
        mappings.put(StoreType.NONE, new Configurations(NoOpSessionConfiguration.class, NoOpReactiveSessionConfiguration.class));
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private SessionStoreMappings() {
    }

    static String getConfigurationClass(WebApplicationType webApplicationType, StoreType sessionStoreType) {
        Configurations configurations = MAPPINGS.get(sessionStoreType);
        Assert.state(configurations != null, (Supplier<String>) () -> {
            return "Unknown session store type " + sessionStoreType;
        });
        return configurations.getConfiguration(webApplicationType);
    }

    static StoreType getType(WebApplicationType webApplicationType, String configurationClass) {
        return (StoreType) MAPPINGS.entrySet().stream().filter(entry -> {
            return ObjectUtils.nullSafeEquals(configurationClass, ((Configurations) entry.getValue()).getConfiguration(webApplicationType));
        }).map((v0) -> {
            return v0.getKey();
        }).findFirst().orElseThrow(() -> {
            return new IllegalStateException("Unknown configuration class " + configurationClass);
        });
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/SessionStoreMappings$Configurations.class */
    private static class Configurations {
        private final Class<?> servletConfiguration;
        private final Class<?> reactiveConfiguration;

        Configurations(Class<?> servletConfiguration, Class<?> reactiveConfiguration) {
            this.servletConfiguration = servletConfiguration;
            this.reactiveConfiguration = reactiveConfiguration;
        }

        String getConfiguration(WebApplicationType webApplicationType) {
            switch (webApplicationType) {
                case SERVLET:
                    return getName(this.servletConfiguration);
                case REACTIVE:
                    return getName(this.reactiveConfiguration);
                default:
                    return null;
            }
        }

        String getName(Class<?> configuration) {
            if (configuration != null) {
                return configuration.getName();
            }
            return null;
        }
    }
}
