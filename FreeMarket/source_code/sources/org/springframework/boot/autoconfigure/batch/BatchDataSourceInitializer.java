package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchDataSourceInitializer.class */
public class BatchDataSourceInitializer extends AbstractDataSourceInitializer {
    private final BatchProperties.Jdbc jdbcProperties;

    public BatchDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, BatchProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "BatchProperties must not be null");
        this.jdbcProperties = properties.getJdbc();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected DataSourceInitializationMode getMode() {
        DatabaseInitializationMode mode = this.jdbcProperties.getInitializeSchema();
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
        return this.jdbcProperties.getSchema();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getDatabaseName() {
        String platform = this.jdbcProperties.getPlatform();
        if (StringUtils.hasText(platform)) {
            return platform;
        }
        String databaseName = super.getDatabaseName();
        if ("oracle".equals(databaseName)) {
            return "oracle10g";
        }
        if ("mariadb".equals(databaseName)) {
            return "mysql";
        }
        return databaseName;
    }
}
