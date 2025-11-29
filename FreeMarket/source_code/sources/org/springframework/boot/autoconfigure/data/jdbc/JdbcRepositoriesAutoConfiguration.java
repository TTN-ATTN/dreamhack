package org.springframework.boot.autoconfigure.data.jdbc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ConditionalOnClass({NamedParameterJdbcOperations.class, AbstractJdbcConfiguration.class})
@ConditionalOnBean({NamedParameterJdbcOperations.class, PlatformTransactionManager.class})
@ConditionalOnProperty(prefix = "spring.data.jdbc.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration.class */
public class JdbcRepositoriesAutoConfiguration {

    @ConditionalOnMissingBean({JdbcRepositoryConfigExtension.class})
    @Configuration(proxyBeanMethods = false)
    @Import({JdbcRepositoriesRegistrar.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration$JdbcRepositoriesConfiguration.class */
    static class JdbcRepositoriesConfiguration {
        JdbcRepositoriesConfiguration() {
        }
    }

    @ConditionalOnMissingBean({AbstractJdbcConfiguration.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration$SpringBootJdbcConfiguration.class */
    static class SpringBootJdbcConfiguration extends AbstractJdbcConfiguration {
        SpringBootJdbcConfiguration() {
        }
    }
}
