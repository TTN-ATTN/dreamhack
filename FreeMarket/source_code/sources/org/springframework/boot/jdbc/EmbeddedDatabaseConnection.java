package org.springframework.boot.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection.class */
public enum EmbeddedDatabaseConnection {
    NONE(null, null, null, url -> {
        return false;
    }),
    H2(EmbeddedDatabaseType.H2, DatabaseDriver.H2.getDriverClassName(), "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", url2 -> {
        return url2.contains(":h2:mem");
    }),
    DERBY(EmbeddedDatabaseType.DERBY, DatabaseDriver.DERBY.getDriverClassName(), "jdbc:derby:memory:%s;create=true", url3 -> {
        return true;
    }),
    HSQLDB(EmbeddedDatabaseType.HSQL, DatabaseDriver.HSQLDB.getDriverClassName(), "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:%s", url4 -> {
        return url4.contains(":hsqldb:mem:");
    });

    private final EmbeddedDatabaseType type;
    private final String driverClass;
    private final String alternativeDriverClass;
    private final String url;
    private final Predicate<String> embeddedUrl;

    EmbeddedDatabaseConnection(EmbeddedDatabaseType type, String driverClass, String url, Predicate embeddedUrl) {
        this(type, driverClass, null, url, embeddedUrl);
    }

    EmbeddedDatabaseConnection(EmbeddedDatabaseType type, String driverClass, String fallbackDriverClass, String url, Predicate embeddedUrl) {
        this.type = type;
        this.driverClass = driverClass;
        this.alternativeDriverClass = fallbackDriverClass;
        this.url = url;
        this.embeddedUrl = embeddedUrl;
    }

    public String getDriverClassName() {
        return this.driverClass;
    }

    public EmbeddedDatabaseType getType() {
        return this.type;
    }

    public String getUrl(String databaseName) {
        Assert.hasText(databaseName, "DatabaseName must not be empty");
        if (this.url != null) {
            return String.format(this.url, databaseName);
        }
        return null;
    }

    boolean isEmbeddedUrl(String url) {
        return this.embeddedUrl.test(url);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDriverCompatible(String driverClass) {
        return driverClass != null && (driverClass.equals(this.driverClass) || driverClass.equals(this.alternativeDriverClass));
    }

    public static boolean isEmbedded(String driverClass, String url) {
        EmbeddedDatabaseConnection connection;
        if (driverClass == null || (connection = getEmbeddedDatabaseConnection(driverClass)) == NONE) {
            return false;
        }
        return url == null || connection.isEmbeddedUrl(url);
    }

    private static EmbeddedDatabaseConnection getEmbeddedDatabaseConnection(String driverClass) {
        return (EmbeddedDatabaseConnection) Stream.of((Object[]) new EmbeddedDatabaseConnection[]{H2, HSQLDB, DERBY}).filter(connection -> {
            return connection.isDriverCompatible(driverClass);
        }).findFirst().orElse(NONE);
    }

    public static boolean isEmbedded(DataSource dataSource) {
        try {
            return ((Boolean) new JdbcTemplate(dataSource).execute(new IsEmbedded())).booleanValue();
        } catch (DataAccessException e) {
            return false;
        }
    }

    public static EmbeddedDatabaseConnection get(ClassLoader classLoader) {
        for (EmbeddedDatabaseConnection candidate : values()) {
            if (candidate != NONE && ClassUtils.isPresent(candidate.getDriverClassName(), classLoader)) {
                return candidate;
            }
        }
        return NONE;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection$IsEmbedded.class */
    private static class IsEmbedded implements ConnectionCallback<Boolean> {
        private IsEmbedded() {
        }

        /* renamed from: doInConnection, reason: merged with bridge method [inline-methods] */
        public Boolean m1566doInConnection(Connection connection) throws SQLException, DataAccessException {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            if (productName == null) {
                return false;
            }
            String productName2 = productName.toUpperCase(Locale.ENGLISH);
            EmbeddedDatabaseConnection[] candidates = EmbeddedDatabaseConnection.values();
            for (EmbeddedDatabaseConnection candidate : candidates) {
                if (candidate != EmbeddedDatabaseConnection.NONE && productName2.contains(candidate.getType().name())) {
                    String url = metaData.getURL();
                    return Boolean.valueOf(url == null || candidate.isEmbeddedUrl(url));
                }
            }
            return false;
        }
    }
}
