package org.springframework.boot.autoconfigure.batch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;

@ConfigurationProperties(prefix = "spring.batch")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchProperties.class */
public class BatchProperties {
    private final Job job = new Job();
    private final Jdbc jdbc = new Jdbc();

    public Job getJob() {
        return this.job;
    }

    public Jdbc getJdbc() {
        return this.jdbc;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Job.class */
    public static class Job {
        private String names = "";

        public String getNames() {
            return this.names;
        }

        public void setNames(String names) {
            this.names = names;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Jdbc.class */
    public static class Jdbc {
        private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/batch/core/schema-@@platform@@.sql";
        private Isolation isolationLevelForCreate;
        private String platform;
        private String tablePrefix;
        private String schema = DEFAULT_SCHEMA_LOCATION;
        private DatabaseInitializationMode initializeSchema = DatabaseInitializationMode.EMBEDDED;

        public Isolation getIsolationLevelForCreate() {
            return this.isolationLevelForCreate;
        }

        public void setIsolationLevelForCreate(Isolation isolationLevelForCreate) {
            this.isolationLevelForCreate = isolationLevelForCreate;
        }

        public String getSchema() {
            return this.schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getPlatform() {
            return this.platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTablePrefix() {
            return this.tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public DatabaseInitializationMode getInitializeSchema() {
            return this.initializeSchema;
        }

        public void setInitializeSchema(DatabaseInitializationMode initializeSchema) {
            this.initializeSchema = initializeSchema;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Isolation.class */
    public enum Isolation {
        DEFAULT,
        READ_UNCOMMITTED,
        READ_COMMITTED,
        REPEATABLE_READ,
        SERIALIZABLE;

        private static final String PREFIX = "ISOLATION_";

        String toIsolationName() {
            return PREFIX + name();
        }
    }
}
