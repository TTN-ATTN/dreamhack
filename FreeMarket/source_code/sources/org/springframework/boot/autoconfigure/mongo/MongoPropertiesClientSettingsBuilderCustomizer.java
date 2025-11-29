package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Collections;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/mongo/MongoPropertiesClientSettingsBuilderCustomizer.class */
public class MongoPropertiesClientSettingsBuilderCustomizer implements MongoClientSettingsBuilderCustomizer, Ordered {
    private final MongoProperties properties;
    private final Environment environment;
    private int order = 0;

    public MongoPropertiesClientSettingsBuilderCustomizer(MongoProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Override // org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
    public void customize(MongoClientSettings.Builder settingsBuilder) {
        applyUuidRepresentation(settingsBuilder);
        applyHostAndPort(settingsBuilder);
        applyCredentials(settingsBuilder);
        applyReplicaSet(settingsBuilder);
    }

    private void applyUuidRepresentation(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.uuidRepresentation(this.properties.getUuidRepresentation());
    }

    private void applyHostAndPort(MongoClientSettings.Builder settings) {
        if (getEmbeddedPort() != null) {
            settings.applyConnectionString(new ConnectionString("mongodb://localhost:" + getEmbeddedPort()));
            return;
        }
        if (this.properties.getUri() != null) {
            settings.applyConnectionString(new ConnectionString(this.properties.getUri()));
            return;
        }
        if (this.properties.getHost() != null || this.properties.getPort() != null) {
            String host = (String) getOrDefault(this.properties.getHost(), "localhost");
            int port = ((Integer) getOrDefault(this.properties.getPort(), Integer.valueOf(MongoProperties.DEFAULT_PORT))).intValue();
            ServerAddress serverAddress = new ServerAddress(host, port);
            settings.applyToClusterSettings(cluster -> {
                cluster.hosts(Collections.singletonList(serverAddress));
            });
            return;
        }
        settings.applyConnectionString(new ConnectionString(MongoProperties.DEFAULT_URI));
    }

    private void applyCredentials(MongoClientSettings.Builder builder) {
        if (this.properties.getUri() == null && this.properties.getUsername() != null && this.properties.getPassword() != null) {
            String database = this.properties.getAuthenticationDatabase() != null ? this.properties.getAuthenticationDatabase() : this.properties.getMongoClientDatabase();
            builder.credential(MongoCredential.createCredential(this.properties.getUsername(), database, this.properties.getPassword()));
        }
    }

    private void applyReplicaSet(MongoClientSettings.Builder builder) {
        if (this.properties.getReplicaSetName() != null) {
            builder.applyToClusterSettings(cluster -> {
                cluster.requiredReplicaSetName(this.properties.getReplicaSetName());
            });
        }
    }

    private <V> V getOrDefault(V value, V defaultValue) {
        return value != null ? value : defaultValue;
    }

    private Integer getEmbeddedPort() {
        String localPort;
        if (this.environment != null && (localPort = this.environment.getProperty("local.mongo.port")) != null) {
            return Integer.valueOf(localPort);
        }
        return null;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
