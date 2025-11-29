package org.springframework.boot.autoconfigure.quartz;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.AbstractDataSourceInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/quartz/QuartzDataSourceInitializer.class */
public class QuartzDataSourceInitializer extends AbstractDataSourceInitializer {
    private final QuartzProperties properties;

    public QuartzDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader, QuartzProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "QuartzProperties must not be null");
        this.properties = properties;
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected void customize(ResourceDatabasePopulator populator) {
        populator.setCommentPrefixes((String[]) this.properties.getJdbc().getCommentPrefix().toArray(new String[0]));
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected DataSourceInitializationMode getMode() {
        DatabaseInitializationMode mode = this.properties.getJdbc().getInitializeSchema();
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
        return this.properties.getJdbc().getSchema();
    }

    @Override // org.springframework.boot.jdbc.AbstractDataSourceInitializer
    protected String getDatabaseName() {
        String platform = this.properties.getJdbc().getPlatform();
        if (StringUtils.hasText(platform)) {
            return platform;
        }
        String databaseName = super.getDatabaseName();
        if ("db2".equals(databaseName)) {
            return "db2_v95";
        }
        if ("mysql".equals(databaseName) || "mariadb".equals(databaseName)) {
            return "mysql_innodb";
        }
        if ("postgresql".equals(databaseName)) {
            return "postgres";
        }
        if ("sqlserver".equals(databaseName)) {
            return "sqlServer";
        }
        return databaseName;
    }
}
