package org.springframework.boot.autoconfigure.integration;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/integration/IntegrationDataSourceInitializer.class */
public class IntegrationDataSourceInitializer extends AbstractDataSourceInitializer {
    private final IntegrationProperties.Jdbc properties;

    public IntegrationDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, IntegrationProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "IntegrationProperties must not be null");
        this.properties = properties.getJdbc();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected DataSourceInitializationMode getMode() {
        DatabaseInitializationMode mode = this.properties.getInitializeSchema();
        switch (mode) {
            case ALWAYS:
                return DataSourceInitializationMode.ALWAYS;
            case EMBEDDED:
                return DataSourceInitializationMode.EMBEDDED;
            case NEVER:
            default:
                return DataSourceInitializationMode.NEVER;
        }
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getSchemaLocation() {
        return this.properties.getSchema();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getDatabaseName() {
        String platform = this.properties.getPlatform();
        if (StringUtils.hasText(platform)) {
            return platform;
        }
        return super.getDatabaseName();
    }
}
