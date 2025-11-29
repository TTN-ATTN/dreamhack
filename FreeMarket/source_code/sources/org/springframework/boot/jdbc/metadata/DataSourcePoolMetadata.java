package org.springframework.boot.jdbc.metadata;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jdbc/metadata/DataSourcePoolMetadata.class */
public interface DataSourcePoolMetadata {
    Float getUsage();

    Integer getActive();

    Integer getMax();

    Integer getMin();

    String getValidationQuery();

    Boolean getDefaultAutoCommit();

    default Integer getIdle() {
        return null;
    }
}
