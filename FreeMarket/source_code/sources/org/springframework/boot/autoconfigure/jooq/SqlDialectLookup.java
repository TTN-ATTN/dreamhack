package org.springframework.boot.autoconfigure.jooq;

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jooq/SqlDialectLookup.class */
final class SqlDialectLookup {
    private static final Log logger = LogFactory.getLog((Class<?>) SqlDialectLookup.class);

    private SqlDialectLookup() {
    }

    static SQLDialect getDialect(DataSource dataSource) {
        if (dataSource == null) {
            return SQLDialect.DEFAULT;
        }
        try {
            String url = (String) JdbcUtils.extractDatabaseMetaData(dataSource, (v0) -> {
                return v0.getURL();
            });
            SQLDialect sqlDialect = JDBCUtils.dialect(url);
            if (sqlDialect != null) {
                return sqlDialect;
            }
        } catch (MetaDataAccessException e) {
            logger.warn("Unable to determine jdbc url from datasource", e);
        }
        return SQLDialect.DEFAULT;
    }
}
