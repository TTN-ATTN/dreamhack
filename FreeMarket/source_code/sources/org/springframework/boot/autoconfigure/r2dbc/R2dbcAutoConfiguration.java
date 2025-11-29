package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryConfigurations;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({R2dbcProperties.class})
@AutoConfiguration(before = {DataSourceAutoConfiguration.class, SqlInitializationAutoConfiguration.class})
@ConditionalOnClass({ConnectionFactory.class})
@ConditionalOnResource(resources = {"classpath:META-INF/services/io.r2dbc.spi.ConnectionFactoryProvider"})
@Import({ConnectionFactoryConfigurations.PoolConfiguration.class, ConnectionFactoryConfigurations.GenericConfiguration.class, ConnectionFactoryDependentConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcAutoConfiguration.class */
public class R2dbcAutoConfiguration {
}
