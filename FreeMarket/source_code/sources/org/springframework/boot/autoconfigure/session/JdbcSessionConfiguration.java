package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.SpringSessionDataSource;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;

@EnableConfigurationProperties({JdbcSessionProperties.class})
@ConditionalOnMissingBean({SessionRepository.class})
@Import({DatabaseInitializationDependencyConfigurer.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({JdbcTemplate.class, JdbcIndexedSessionRepository.class})
@ConditionalOnBean({DataSource.class})
@Conditional({ServletSessionCondition.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration.class */
class JdbcSessionConfiguration {
    JdbcSessionConfiguration() {
    }

    @ConditionalOnMissingBean({JdbcSessionDataSourceScriptDatabaseInitializer.class, JdbcSessionDataSourceInitializer.class})
    @Conditional({OnJdbcSessionDatasourceInitializationCondition.class})
    @Bean
    JdbcSessionDataSourceScriptDatabaseInitializer jdbcSessionDataSourceScriptDatabaseInitializer(@SpringSessionDataSource ObjectProvider<DataSource> sessionDataSource, ObjectProvider<DataSource> dataSource, JdbcSessionProperties properties) throws BeansException {
        dataSource.getClass();
        DataSource dataSourceToInitialize = sessionDataSource.getIfAvailable(dataSource::getObject);
        return new JdbcSessionDataSourceScriptDatabaseInitializer(dataSourceToInitialize, properties);
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration$SpringBootJdbcHttpSessionConfiguration.class */
    static class SpringBootJdbcHttpSessionConfiguration extends JdbcHttpSessionConfiguration {
        SpringBootJdbcHttpSessionConfiguration() {
        }

        @Autowired
        void customize(SessionProperties sessionProperties, JdbcSessionProperties jdbcSessionProperties, ServerProperties serverProperties) {
            Duration timeout = sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
            });
            if (timeout != null) {
                setMaxInactiveIntervalInSeconds(Integer.valueOf((int) timeout.getSeconds()));
            }
            setTableName(jdbcSessionProperties.getTableName());
            setCleanupCron(jdbcSessionProperties.getCleanupCron());
            setFlushMode(jdbcSessionProperties.getFlushMode());
            setSaveMode(jdbcSessionProperties.getSaveMode());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration$OnJdbcSessionDatasourceInitializationCondition.class */
    static class OnJdbcSessionDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
        OnJdbcSessionDatasourceInitializationCondition() {
            super("Jdbc Session", "spring.session.jdbc.initialize-schema");
        }
    }
}
