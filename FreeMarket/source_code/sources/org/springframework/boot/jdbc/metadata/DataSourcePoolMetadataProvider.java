package org.springframework.boot.jdbc.metadata;

import javax.sql.DataSource;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/metadata/DataSourcePoolMetadataProvider.class */
public interface DataSourcePoolMetadataProvider {
    DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource);
}
