package org.springframework.boot.jdbc;

import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/AbstractDataSourceInitializer.class */
public abstract class AbstractDataSourceInitializer implements InitializingBean {
    private static final String PLATFORM_PLACEHOLDER = "@@platform@@";
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    protected abstract DataSourceInitializationMode getMode();

    protected abstract String getSchemaLocation();

    protected AbstractDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        Assert.notNull(dataSource, "DataSource must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initialize();
    }

    protected void initialize() {
        if (!isEnabled()) {
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String schemaLocation = getSchemaLocation();
        if (schemaLocation.contains(PLATFORM_PLACEHOLDER)) {
            String platform = getDatabaseName();
            schemaLocation = schemaLocation.replace(PLATFORM_PLACEHOLDER, platform);
        }
        populator.addScript(this.resourceLoader.getResource(schemaLocation));
        populator.setContinueOnError(true);
        customize(populator);
        DatabasePopulatorUtils.execute(populator, this.dataSource);
    }

    private boolean isEnabled() {
        if (getMode() == DataSourceInitializationMode.NEVER) {
            return false;
        }
        return getMode() != DataSourceInitializationMode.EMBEDDED || EmbeddedDatabaseConnection.isEmbedded(this.dataSource);
    }

    protected void customize(ResourceDatabasePopulator populator) {
    }

    protected String getDatabaseName() {
        try {
            String productName = JdbcUtils.commonDatabaseName((String) JdbcUtils.extractDatabaseMetaData(this.dataSource, (v0) -> {
                return v0.getDatabaseProductName();
            }));
            DatabaseDriver databaseDriver = DatabaseDriver.fromProductName(productName);
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                throw new IllegalStateException("Unable to detect database type");
            }
            return databaseDriver.getId();
        } catch (MetaDataAccessException ex) {
            throw new IllegalStateException("Unable to detect database type", ex);
        }
    }
}
