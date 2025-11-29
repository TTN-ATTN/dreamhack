package org.springframework.boot.autoconfigure.jooq;

import javax.sql.DataSource;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.ExecutorProvider;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordUnmapperProvider;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, TransactionAutoConfiguration.class})
@ConditionalOnClass({DSLContext.class})
@ConditionalOnBean({DataSource.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jooq/JooqAutoConfiguration.class */
public class JooqAutoConfiguration {
    @ConditionalOnMissingBean({ConnectionProvider.class})
    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @ConditionalOnMissingBean({TransactionProvider.class})
    @ConditionalOnBean({PlatformTransactionManager.class})
    @Bean
    public SpringTransactionProvider transactionProvider(PlatformTransactionManager txManager) {
        return new SpringTransactionProvider(txManager);
    }

    @Bean
    @Order(0)
    public DefaultExecuteListenerProvider jooqExceptionTranslatorExecuteListenerProvider() {
        return new DefaultExecuteListenerProvider(new JooqExceptionTranslator());
    }

    @ConditionalOnMissingBean({DSLContext.class})
    @EnableConfigurationProperties({JooqProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jooq/JooqAutoConfiguration$DslContextConfiguration.class */
    public static class DslContextConfiguration {
        @Bean
        public DefaultDSLContext dslContext(org.jooq.Configuration configuration) {
            return new DefaultDSLContext(configuration);
        }

        @ConditionalOnMissingBean({org.jooq.Configuration.class})
        @Bean
        public DefaultConfiguration jooqConfiguration(JooqProperties properties, ConnectionProvider connectionProvider, DataSource dataSource, ObjectProvider<ExecuteListenerProvider> executeListenerProviders, ObjectProvider<DefaultConfigurationCustomizer> configurationCustomizers) {
            DefaultConfiguration configuration = new DefaultConfiguration();
            configuration.set(properties.determineSqlDialect(dataSource));
            configuration.set(connectionProvider);
            configuration.set((ExecuteListenerProvider[]) executeListenerProviders.orderedStream().toArray(x$0 -> {
                return new ExecuteListenerProvider[x$0];
            }));
            configurationCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configuration);
            });
            return configuration;
        }

        @Bean
        @Deprecated
        public DefaultConfigurationCustomizer jooqProvidersDefaultConfigurationCustomizer(ObjectProvider<TransactionProvider> transactionProvider, ObjectProvider<RecordMapperProvider> recordMapperProvider, ObjectProvider<RecordUnmapperProvider> recordUnmapperProvider, ObjectProvider<Settings> settings, ObjectProvider<RecordListenerProvider> recordListenerProviders, ObjectProvider<VisitListenerProvider> visitListenerProviders, ObjectProvider<TransactionListenerProvider> transactionListenerProviders, ObjectProvider<ExecutorProvider> executorProvider) {
            return new OrderedDefaultConfigurationCustomizer(configuration -> {
                configuration.getClass();
                transactionProvider.ifAvailable(configuration::set);
                configuration.getClass();
                recordMapperProvider.ifAvailable(configuration::set);
                configuration.getClass();
                recordUnmapperProvider.ifAvailable(configuration::set);
                configuration.getClass();
                settings.ifAvailable(configuration::set);
                configuration.getClass();
                executorProvider.ifAvailable(configuration::set);
                configuration.set((RecordListenerProvider[]) recordListenerProviders.orderedStream().toArray(x$0 -> {
                    return new RecordListenerProvider[x$0];
                }));
                configuration.set((VisitListenerProvider[]) visitListenerProviders.orderedStream().toArray(x$02 -> {
                    return new VisitListenerProvider[x$02];
                }));
                configuration.setTransactionListenerProvider((TransactionListenerProvider[]) transactionListenerProviders.orderedStream().toArray(x$03 -> {
                    return new TransactionListenerProvider[x$03];
                }));
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/jooq/JooqAutoConfiguration$OrderedDefaultConfigurationCustomizer.class */
    private static class OrderedDefaultConfigurationCustomizer implements DefaultConfigurationCustomizer, Ordered {
        private final DefaultConfigurationCustomizer delegate;

        OrderedDefaultConfigurationCustomizer(DefaultConfigurationCustomizer delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
        public void customize(DefaultConfiguration configuration) {
            this.delegate.customize(configuration);
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }
    }
}
