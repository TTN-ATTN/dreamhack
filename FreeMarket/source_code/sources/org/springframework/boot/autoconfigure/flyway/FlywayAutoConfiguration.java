package org.springframework.boot.autoconfigure.flyway;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.internal.plugin.PluginRegister;
import org.flywaydb.database.sqlserver.SQLServerConfigurationExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnClass({Flyway.class})
@Conditional({FlywayDataSourceCondition.class})
@ConditionalOnProperty(prefix = "spring.flyway", name = {"enabled"}, matchIfMissing = true)
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration.class */
public class FlywayAutoConfiguration {
    @ConfigurationPropertiesBinding
    @Bean
    public StringOrNumberToMigrationVersionConverter stringOrNumberMigrationVersionConverter() {
        return new StringOrNumberToMigrationVersionConverter();
    }

    @Bean
    public FlywaySchemaManagementProvider flywayDefaultDdlModeProvider(ObjectProvider<Flyway> flyways) {
        return new FlywaySchemaManagementProvider(flyways);
    }

    @EnableConfigurationProperties({FlywayProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JdbcUtils.class})
    @ConditionalOnMissingBean({Flyway.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration.class */
    public static class FlywayConfiguration {
        @Bean
        public Flyway flyway(FlywayProperties properties, ResourceLoader resourceLoader, ObjectProvider<DataSource> dataSource, @FlywayDataSource ObjectProvider<DataSource> flywayDataSource, ObjectProvider<FlywayConfigurationCustomizer> fluentConfigurationCustomizers, ObjectProvider<JavaMigration> javaMigrations, ObjectProvider<Callback> callbacks) {
            FluentConfiguration configuration = new FluentConfiguration(resourceLoader.getClassLoader());
            configureDataSource(configuration, properties, flywayDataSource.getIfAvailable(), dataSource.getIfUnique());
            configureProperties(configuration, properties);
            List<Callback> orderedCallbacks = (List) callbacks.orderedStream().collect(Collectors.toList());
            configureCallbacks(configuration, orderedCallbacks);
            fluentConfigurationCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configuration);
            });
            configureFlywayCallbacks(configuration, orderedCallbacks);
            List<JavaMigration> migrations = (List) javaMigrations.stream().collect(Collectors.toList());
            configureJavaMigrations(configuration, migrations);
            return configuration.load();
        }

        private void configureDataSource(FluentConfiguration configuration, FlywayProperties properties, DataSource flywayDataSource, DataSource dataSource) {
            DataSource migrationDataSource = getMigrationDataSource(properties, flywayDataSource, dataSource);
            configuration.dataSource(migrationDataSource);
        }

        private DataSource getMigrationDataSource(FlywayProperties properties, DataSource flywayDataSource, DataSource dataSource) {
            if (flywayDataSource != null) {
                return flywayDataSource;
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
            Assert.state(dataSource != null, "Flyway migration DataSource missing");
            return dataSource;
        }

        private void applyCommonBuilderProperties(FlywayProperties properties, DataSourceBuilder<?> builder) {
            builder.username(properties.getUser());
            builder.password(properties.getPassword());
            if (StringUtils.hasText(properties.getDriverClassName())) {
                builder.driverClassName(properties.getDriverClassName());
            }
        }

        private void configureProperties(FluentConfiguration configuration, FlywayProperties properties) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            String[] locations = (String[]) new LocationResolver(configuration.getDataSource()).resolveLocations(properties.getLocations()).toArray(new String[0]);
            configureFailOnMissingLocations(configuration, properties.isFailOnMissingLocations());
            PropertyMapper.Source sourceFrom = map.from((PropertyMapper) locations);
            configuration.getClass();
            sourceFrom.to(configuration::locations);
            PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) properties.getEncoding());
            configuration.getClass();
            sourceFrom2.to(configuration::encoding);
            PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) Integer.valueOf(properties.getConnectRetries()));
            configuration.getClass();
            sourceFrom3.to((v1) -> {
                r1.connectRetries(v1);
            });
            map.from((PropertyMapper) properties.getConnectRetriesInterval()).to(interval -> {
                configuration.connectRetriesInterval((int) interval.getSeconds());
            });
            map.from((PropertyMapper) properties.getLockRetryCount()).to(lockRetryCount -> {
                configuration.lockRetryCount(lockRetryCount.intValue());
            });
            map.from((PropertyMapper) properties.getDefaultSchema()).to(schema -> {
                configuration.defaultSchema(schema);
            });
            PropertyMapper.Source sourceAs = map.from((PropertyMapper) properties.getSchemas()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            });
            configuration.getClass();
            sourceAs.to(configuration::schemas);
            configureCreateSchemas(configuration, properties.isCreateSchemas());
            PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) properties.getTable());
            configuration.getClass();
            sourceFrom4.to(configuration::table);
            map.from((PropertyMapper) properties.getTablespace()).to(tablespace -> {
                configuration.tablespace(tablespace);
            });
            PropertyMapper.Source sourceFrom5 = map.from((PropertyMapper) properties.getBaselineDescription());
            configuration.getClass();
            sourceFrom5.to(configuration::baselineDescription);
            PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) properties.getBaselineVersion());
            configuration.getClass();
            sourceFrom6.to(configuration::baselineVersion);
            PropertyMapper.Source sourceFrom7 = map.from((PropertyMapper) properties.getInstalledBy());
            configuration.getClass();
            sourceFrom7.to(configuration::installedBy);
            PropertyMapper.Source sourceFrom8 = map.from((PropertyMapper) properties.getPlaceholders());
            configuration.getClass();
            sourceFrom8.to(configuration::placeholders);
            PropertyMapper.Source sourceFrom9 = map.from((PropertyMapper) properties.getPlaceholderPrefix());
            configuration.getClass();
            sourceFrom9.to(configuration::placeholderPrefix);
            PropertyMapper.Source sourceFrom10 = map.from((PropertyMapper) properties.getPlaceholderSuffix());
            configuration.getClass();
            sourceFrom10.to(configuration::placeholderSuffix);
            map.from((PropertyMapper) properties.getPlaceholderSeparator()).to(placeHolderSeparator -> {
                configuration.placeholderSeparator(placeHolderSeparator);
            });
            PropertyMapper.Source sourceFrom11 = map.from((PropertyMapper) Boolean.valueOf(properties.isPlaceholderReplacement()));
            configuration.getClass();
            sourceFrom11.to((v1) -> {
                r1.placeholderReplacement(v1);
            });
            PropertyMapper.Source sourceFrom12 = map.from((PropertyMapper) properties.getSqlMigrationPrefix());
            configuration.getClass();
            sourceFrom12.to(configuration::sqlMigrationPrefix);
            PropertyMapper.Source sourceAs2 = map.from((PropertyMapper) properties.getSqlMigrationSuffixes()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            });
            configuration.getClass();
            sourceAs2.to(configuration::sqlMigrationSuffixes);
            PropertyMapper.Source sourceFrom13 = map.from((PropertyMapper) properties.getSqlMigrationSeparator());
            configuration.getClass();
            sourceFrom13.to(configuration::sqlMigrationSeparator);
            PropertyMapper.Source sourceFrom14 = map.from((PropertyMapper) properties.getRepeatableSqlMigrationPrefix());
            configuration.getClass();
            sourceFrom14.to(configuration::repeatableSqlMigrationPrefix);
            PropertyMapper.Source sourceFrom15 = map.from((PropertyMapper) properties.getTarget());
            configuration.getClass();
            sourceFrom15.to(configuration::target);
            PropertyMapper.Source sourceFrom16 = map.from((PropertyMapper) Boolean.valueOf(properties.isBaselineOnMigrate()));
            configuration.getClass();
            sourceFrom16.to((v1) -> {
                r1.baselineOnMigrate(v1);
            });
            PropertyMapper.Source sourceFrom17 = map.from((PropertyMapper) Boolean.valueOf(properties.isCleanDisabled()));
            configuration.getClass();
            sourceFrom17.to((v1) -> {
                r1.cleanDisabled(v1);
            });
            PropertyMapper.Source sourceFrom18 = map.from((PropertyMapper) Boolean.valueOf(properties.isCleanOnValidationError()));
            configuration.getClass();
            sourceFrom18.to((v1) -> {
                r1.cleanOnValidationError(v1);
            });
            PropertyMapper.Source sourceFrom19 = map.from((PropertyMapper) Boolean.valueOf(properties.isGroup()));
            configuration.getClass();
            sourceFrom19.to((v1) -> {
                r1.group(v1);
            });
            configureIgnoredMigrations(configuration, properties, map);
            PropertyMapper.Source sourceFrom20 = map.from((PropertyMapper) Boolean.valueOf(properties.isMixed()));
            configuration.getClass();
            sourceFrom20.to((v1) -> {
                r1.mixed(v1);
            });
            PropertyMapper.Source sourceFrom21 = map.from((PropertyMapper) Boolean.valueOf(properties.isOutOfOrder()));
            configuration.getClass();
            sourceFrom21.to((v1) -> {
                r1.outOfOrder(v1);
            });
            PropertyMapper.Source sourceFrom22 = map.from((PropertyMapper) Boolean.valueOf(properties.isSkipDefaultCallbacks()));
            configuration.getClass();
            sourceFrom22.to((v1) -> {
                r1.skipDefaultCallbacks(v1);
            });
            PropertyMapper.Source sourceFrom23 = map.from((PropertyMapper) Boolean.valueOf(properties.isSkipDefaultResolvers()));
            configuration.getClass();
            sourceFrom23.to((v1) -> {
                r1.skipDefaultResolvers(v1);
            });
            configureValidateMigrationNaming(configuration, properties.isValidateMigrationNaming());
            PropertyMapper.Source sourceFrom24 = map.from((PropertyMapper) Boolean.valueOf(properties.isValidateOnMigrate()));
            configuration.getClass();
            sourceFrom24.to((v1) -> {
                r1.validateOnMigrate(v1);
            });
            PropertyMapper.Source sourceAs3 = map.from((PropertyMapper) properties.getInitSqls()).whenNot((v0) -> {
                return CollectionUtils.isEmpty(v0);
            }).as(initSqls -> {
                return StringUtils.collectionToDelimitedString(initSqls, "\n");
            });
            configuration.getClass();
            sourceAs3.to(configuration::initSql);
            map.from((PropertyMapper) properties.getScriptPlaceholderPrefix()).to(prefix -> {
                configuration.scriptPlaceholderPrefix(prefix);
            });
            map.from((PropertyMapper) properties.getScriptPlaceholderSuffix()).to(suffix -> {
                configuration.scriptPlaceholderSuffix(suffix);
            });
            PropertyMapper.Source sourceFrom25 = map.from((PropertyMapper) properties.getBatch());
            configuration.getClass();
            sourceFrom25.to((v1) -> {
                r1.batch(v1);
            });
            PropertyMapper.Source sourceFrom26 = map.from((PropertyMapper) properties.getDryRunOutput());
            configuration.getClass();
            sourceFrom26.to(configuration::dryRunOutput);
            PropertyMapper.Source sourceFrom27 = map.from((PropertyMapper) properties.getErrorOverrides());
            configuration.getClass();
            sourceFrom27.to(configuration::errorOverrides);
            PropertyMapper.Source sourceFrom28 = map.from((PropertyMapper) properties.getLicenseKey());
            configuration.getClass();
            sourceFrom28.to(configuration::licenseKey);
            PropertyMapper.Source sourceFrom29 = map.from((PropertyMapper) properties.getOracleSqlplus());
            configuration.getClass();
            sourceFrom29.to((v1) -> {
                r1.oracleSqlplus(v1);
            });
            map.from((PropertyMapper) properties.getOracleSqlplusWarn()).to(oracleSqlplusWarn -> {
                configuration.oracleSqlplusWarn(oracleSqlplusWarn.booleanValue());
            });
            PropertyMapper.Source sourceFrom30 = map.from((PropertyMapper) properties.getStream());
            configuration.getClass();
            sourceFrom30.to((v1) -> {
                r1.stream(v1);
            });
            PropertyMapper.Source sourceFrom31 = map.from((PropertyMapper) properties.getUndoSqlMigrationPrefix());
            configuration.getClass();
            sourceFrom31.to(configuration::undoSqlMigrationPrefix);
            map.from((PropertyMapper) properties.getCherryPick()).to(cherryPick -> {
                configuration.cherryPick(cherryPick);
            });
            map.from((PropertyMapper) properties.getJdbcProperties()).whenNot((v0) -> {
                return v0.isEmpty();
            }).to(jdbcProperties -> {
                configuration.jdbcProperties(jdbcProperties);
            });
            map.from((PropertyMapper) properties.getKerberosConfigFile()).to(configFile -> {
                configuration.kerberosConfigFile(configFile);
            });
            map.from((PropertyMapper) properties.getOracleKerberosCacheFile()).to(cacheFile -> {
                configuration.oracleKerberosCacheFile(cacheFile);
            });
            map.from((PropertyMapper) properties.getOutputQueryResults()).to(outputQueryResults -> {
                configuration.outputQueryResults(outputQueryResults.booleanValue());
            });
            map.from((PropertyMapper) properties.getSqlServerKerberosLoginFile()).whenNonNull().to(this::configureSqlServerKerberosLoginFile);
            map.from((PropertyMapper) properties.getSkipExecutingMigrations()).to(skipExecutingMigrations -> {
                configuration.skipExecutingMigrations(skipExecutingMigrations.booleanValue());
            });
            map.from((PropertyMapper) properties.getIgnoreMigrationPatterns()).whenNot((v0) -> {
                return v0.isEmpty();
            }).to(ignoreMigrationPatterns -> {
                configuration.ignoreMigrationPatterns((String[]) ignoreMigrationPatterns.toArray(new String[0]));
            });
            map.from((PropertyMapper) properties.getDetectEncoding()).to(detectEncoding -> {
                configuration.detectEncoding(detectEncoding.booleanValue());
            });
            map.from((PropertyMapper) properties.getBaselineMigrationPrefix()).to(baselineMigrationPrefix -> {
                configuration.baselineMigrationPrefix(baselineMigrationPrefix);
            });
        }

        private void configureIgnoredMigrations(FluentConfiguration configuration, FlywayProperties properties, PropertyMapper map) {
            try {
                PropertyMapper.Source sourceFrom = map.from((PropertyMapper) Boolean.valueOf(properties.isIgnoreMissingMigrations()));
                configuration.getClass();
                sourceFrom.to((v1) -> {
                    r1.ignoreMissingMigrations(v1);
                });
                PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) Boolean.valueOf(properties.isIgnoreIgnoredMigrations()));
                configuration.getClass();
                sourceFrom2.to((v1) -> {
                    r1.ignoreIgnoredMigrations(v1);
                });
                PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) Boolean.valueOf(properties.isIgnorePendingMigrations()));
                configuration.getClass();
                sourceFrom3.to((v1) -> {
                    r1.ignorePendingMigrations(v1);
                });
                PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) Boolean.valueOf(properties.isIgnoreFutureMigrations()));
                configuration.getClass();
                sourceFrom4.to((v1) -> {
                    r1.ignoreFutureMigrations(v1);
                });
            } catch (BootstrapMethodError | NoSuchMethodError e) {
            }
        }

        private void configureFailOnMissingLocations(FluentConfiguration configuration, boolean failOnMissingLocations) {
            try {
                configuration.failOnMissingLocations(failOnMissingLocations);
            } catch (NoSuchMethodError e) {
            }
        }

        private void configureCreateSchemas(FluentConfiguration configuration, boolean createSchemas) {
            try {
                configuration.createSchemas(createSchemas);
            } catch (NoSuchMethodError e) {
            }
        }

        private void configureSqlServerKerberosLoginFile(String sqlServerKerberosLoginFile) {
            SQLServerConfigurationExtension sqlServerConfigurationExtension = PluginRegister.getPlugin(SQLServerConfigurationExtension.class);
            sqlServerConfigurationExtension.setKerberosLoginFile(sqlServerKerberosLoginFile);
        }

        private void configureValidateMigrationNaming(FluentConfiguration configuration, boolean validateMigrationNaming) {
            try {
                configuration.validateMigrationNaming(validateMigrationNaming);
            } catch (NoSuchMethodError e) {
            }
        }

        private void configureCallbacks(FluentConfiguration configuration, List<Callback> callbacks) {
            if (!callbacks.isEmpty()) {
                configuration.callbacks((Callback[]) callbacks.toArray(new Callback[0]));
            }
        }

        private void configureFlywayCallbacks(FluentConfiguration flyway, List<Callback> callbacks) {
            if (!callbacks.isEmpty()) {
                flyway.callbacks((Callback[]) callbacks.toArray(new Callback[0]));
            }
        }

        private void configureJavaMigrations(FluentConfiguration flyway, List<JavaMigration> migrations) {
            if (!migrations.isEmpty()) {
                try {
                    flyway.javaMigrations((JavaMigration[]) migrations.toArray(new JavaMigration[0]));
                } catch (NoSuchMethodError e) {
                }
            }
        }

        @ConditionalOnMissingBean
        @Bean
        public FlywayMigrationInitializer flywayInitializer(Flyway flyway, ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
            return new FlywayMigrationInitializer(flyway, migrationStrategy.getIfAvailable());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$LocationResolver.class */
    private static class LocationResolver {
        private static final String VENDOR_PLACEHOLDER = "{vendor}";
        private final DataSource dataSource;

        LocationResolver(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        List<String> resolveLocations(List<String> locations) {
            if (usesVendorLocation(locations)) {
                DatabaseDriver databaseDriver = getDatabaseDriver();
                return replaceVendorLocations(locations, databaseDriver);
            }
            return locations;
        }

        private List<String> replaceVendorLocations(List<String> locations, DatabaseDriver databaseDriver) {
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                return locations;
            }
            String vendor = databaseDriver.getId();
            return (List) locations.stream().map(location -> {
                return location.replace(VENDOR_PLACEHOLDER, vendor);
            }).collect(Collectors.toList());
        }

        private DatabaseDriver getDatabaseDriver() {
            try {
                String url = (String) JdbcUtils.extractDatabaseMetaData(this.dataSource, (v0) -> {
                    return v0.getURL();
                });
                return DatabaseDriver.fromJdbcUrl(url);
            } catch (MetaDataAccessException ex) {
                throw new IllegalStateException((Throwable) ex);
            }
        }

        private boolean usesVendorLocation(Collection<String> locations) {
            for (String location : locations) {
                if (location.contains(VENDOR_PLACEHOLDER)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$StringOrNumberToMigrationVersionConverter.class */
    static class StringOrNumberToMigrationVersionConverter implements GenericConverter {
        private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_TYPES;

        StringOrNumberToMigrationVersionConverter() {
        }

        static {
            Set<GenericConverter.ConvertiblePair> types = new HashSet<>(2);
            types.add(new GenericConverter.ConvertiblePair(String.class, MigrationVersion.class));
            types.add(new GenericConverter.ConvertiblePair(Number.class, MigrationVersion.class));
            CONVERTIBLE_TYPES = Collections.unmodifiableSet(types);
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return CONVERTIBLE_TYPES;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String value = ObjectUtils.nullSafeToString(source);
            return MigrationVersion.fromVersion(value);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition.class */
    static final class FlywayDataSourceCondition extends AnyNestedCondition {
        FlywayDataSourceCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({DataSource.class})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition$DataSourceBeanCondition.class */
        private static final class DataSourceBeanCondition {
            private DataSourceBeanCondition() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.flyway", name = {"url"})
        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition$FlywayUrlCondition.class */
        private static final class FlywayUrlCondition {
            private FlywayUrlCondition() {
            }
        }
    }
}
