package org.springframework.boot.autoconfigure.orm.jpa;

import javax.persistence.EntityManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableConfigurationProperties({JpaProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class}, before = {TransactionAutoConfiguration.class})
@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, EntityManager.class, SessionImplementor.class})
@Import({HibernateJpaConfiguration.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration.class */
public class HibernateJpaAutoConfiguration {
}
