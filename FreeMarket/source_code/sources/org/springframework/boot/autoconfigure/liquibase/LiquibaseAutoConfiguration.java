package org.springframework.boot.autoconfigure.liquibase;

import javax.sql.DataSource;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnClass({SpringLiquibase.class, DatabaseChange.class})
@ConditionalOnProperty(prefix = "spring.liquibase", name = {"enabled"}, matchIfMissing = true)
@Conditional({LiquibaseDataSourceCondition.class})
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration.class */
public class LiquibaseAutoConfiguration {
    @Bean
    public LiquibaseSchemaManagementProvider liquibaseDefaultDdlModeProvider(ObjectProvider<SpringLiquibase> liquibases) {
        return new LiquibaseSchemaManagementProvider(liquibases);
    }

    @EnableConfigurationProperties({LiquibaseProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ConnectionCallback.class})
    @ConditionalOnMissingBean({SpringLiquibase.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class */
    public static class LiquibaseConfiguration {
        private final LiquibaseProperties properties;

        public LiquibaseConfiguration(LiquibaseProperties properties) {
            this.properties = properties;
        }

        @Bean
        public SpringLiquibase liquibase(ObjectProvider<DataSource> dataSource, @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource) {
            SpringLiquibase liquibase = createSpringLiquibase(liquibaseDataSource.getIfAvailable(), dataSource.getIfUnique());
            liquibase.setChangeLog(this.properties.getChangeLog());
            liquibase.setClearCheckSums(this.properties.isClearChecksums());
            liquibase.setContexts(this.properties.getContexts());
            liquibase.setDefaultSchema(this.properties.getDefaultSchema());
            liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
            liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
            liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
            liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
            liquibase.setDropFirst(this.properties.isDropFirst());
            liquibase.setShouldRun(this.properties.isEnabled());
            liquibase.setLabels(this.properties.getLabels());
            liquibase.setChangeLogParameters(this.properties.getParameters());
            liquibase.setRollbackFile(this.properties.getRollbackFile());
            liquibase.setTestRollbackOnUpdate(this.properties.isTestRollbackOnUpdate());
            liquibase.setTag(this.properties.getTag());
            return liquibase;
        }

        private SpringLiquibase createSpringLiquibase(DataSource liquibaseDataSource, DataSource dataSource) {
            LiquibaseProperties properties = this.properties;
            DataSource migrationDataSource = getMigrationDataSource(liquibaseDataSource, dataSource, properties);
            SpringLiquibase liquibase = (migrationDataSource == liquibaseDataSource || migrationDataSource == dataSource) ? new SpringLiquibase() : new DataSourceClosingSpringLiquibase();
            liquibase.setDataSource(migrationDataSource);
            return liquibase;
        }

        private DataSource getMigrationDataSource(DataSource liquibaseDataSource, DataSource dataSource, LiquibaseProperties properties) {
            if (liquibaseDataSource != null) {
                return liquibaseDataSource;
            }
            if (properties.getUrl() != null) {
                DataSourceBuilder<?> builder = DataSourceBuilder.create().type(SimpleDriverDataSource.class);
                builder.url(properties.getUrl());
                applyCommonBuilderProperties(properties, builder);
                return builder.build();
            }
            if (properties.getUser() != null && dataSource != null) {
                DataSourceBuilder<?> builder2 = DataSourceBuilder.derivedFrom(dataSource).type(SimpleDriverDataSource.class);
                applyCommonBuilderProperties(properties, builder2);
                return builder2.build();
            }
            Assert.state(dataSource != null, "Liquibase migration DataSource missing");
            return dataSource;
        }

        private void applyCommonBuilderProperties(LiquibaseProperties properties, DataSourceBuilder<?> builder) {
            builder.username(properties.getUser());
            builder.password(properties.getPassword());
            if (StringUtils.hasText(properties.getDriverClassName())) {
                builder.driverClassName(properties.getDriverClassName());
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition.class */
    static final class LiquibaseDataSourceCondition extends AnyNestedCondition {
        LiquibaseDataSourceCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({DataSource.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition$DataSourceBeanCondition.class */
        private static final class DataSourceBeanCondition {
            private DataSourceBeanCondition() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.liquibase", name = {"url"})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition$LiquibaseUrlCondition.class */
        private static final class LiquibaseUrlCondition {
            private LiquibaseUrlCondition() {
            }
        }
    }
}
