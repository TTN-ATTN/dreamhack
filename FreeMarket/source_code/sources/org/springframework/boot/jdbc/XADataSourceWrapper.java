package org.springframework.boot.jdbc;

import javax.sql.DataSource;
import javax.sql.XADataSource;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/XADataSourceWrapper.class */
public interface XADataSourceWrapper {
    /* renamed from: wrapDataSource */
    DataSource mo1573wrapDataSource(XADataSource dataSource) throws Exception;
}
