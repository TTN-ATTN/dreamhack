package org.springframework.scheduling.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.impl.jdbcjobstore.SimpleSemaphore;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/LocalDataSourceJobStore.class */
public class LocalDataSourceJobStore extends JobStoreCMT {
    public static final String TX_DATA_SOURCE_PREFIX = "springTxDataSource.";
    public static final String NON_TX_DATA_SOURCE_PREFIX = "springNonTxDataSource.";

    @Nullable
    private DataSource dataSource;

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.quartz.SchedulerConfigException */
    public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler) throws SchedulerConfigException {
        this.dataSource = SchedulerFactoryBean.getConfigTimeDataSource();
        if (this.dataSource == null) {
            throw new SchedulerConfigException("No local DataSource found for configuration - 'dataSource' property must be set on SchedulerFactoryBean");
        }
        setDataSource(TX_DATA_SOURCE_PREFIX + getInstanceName());
        setDontSetAutoCommitFalse(true);
        DBConnectionManager.getInstance().addConnectionProvider(TX_DATA_SOURCE_PREFIX + getInstanceName(), new ConnectionProvider() { // from class: org.springframework.scheduling.quartz.LocalDataSourceJobStore.1
            public Connection getConnection() throws SQLException {
                return DataSourceUtils.doGetConnection(LocalDataSourceJobStore.this.dataSource);
            }

            public void shutdown() {
            }

            public void initialize() {
            }
        });
        DataSource nonTxDataSource = SchedulerFactoryBean.getConfigTimeNonTransactionalDataSource();
        final DataSource nonTxDataSourceToUse = nonTxDataSource != null ? nonTxDataSource : this.dataSource;
        setNonManagedTXDataSource(NON_TX_DATA_SOURCE_PREFIX + getInstanceName());
        DBConnectionManager.getInstance().addConnectionProvider(NON_TX_DATA_SOURCE_PREFIX + getInstanceName(), new ConnectionProvider() { // from class: org.springframework.scheduling.quartz.LocalDataSourceJobStore.2
            public Connection getConnection() throws SQLException {
                return nonTxDataSourceToUse.getConnection();
            }

            public void shutdown() {
            }

            public void initialize() {
            }
        });
        try {
            String productName = JdbcUtils.commonDatabaseName((String) JdbcUtils.extractDatabaseMetaData(this.dataSource, (v0) -> {
                return v0.getDatabaseProductName();
            }));
            if (productName != null && productName.toLowerCase().contains("hsql")) {
                setUseDBLocks(false);
                setLockHandler(new SimpleSemaphore());
            }
        } catch (MetaDataAccessException e) {
            logWarnIfNonZero(1, "Could not detect database type. Assuming locks can be taken.");
        }
        super.initialize(loadHelper, signaler);
    }

    protected void closeConnection(Connection con) {
        DataSourceUtils.releaseConnection(con, this.dataSource);
    }
}
