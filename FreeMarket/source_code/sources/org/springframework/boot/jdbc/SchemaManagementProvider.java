package org.springframework.boot.jdbc;

import javax.sql.DataSource;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/SchemaManagementProvider.class */
public interface SchemaManagementProvider {
    SchemaManagement getSchemaManagement(DataSource dataSource);
}
