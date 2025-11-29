package org.springframework.boot.jta.atomikos;

import javax.sql.XADataSource;
import org.springframework.boot.jdbc.XADataSourceWrapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jta/atomikos/AtomikosXADataSourceWrapper.class */
public class AtomikosXADataSourceWrapper implements XADataSourceWrapper {
    @Override // org.springframework.boot.jdbc.XADataSourceWrapper
    /* renamed from: wrapDataSource, reason: merged with bridge method [inline-methods] */
    public AtomikosDataSourceBean mo1573wrapDataSource(XADataSource dataSource) throws Exception {
        AtomikosDataSourceBean bean = new AtomikosDataSourceBean();
        bean.setXaDataSource(dataSource);
        return bean;
    }
}
