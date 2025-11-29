package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

@AutoConfiguration(after = {CassandraAutoConfiguration.class})
@ConditionalOnClass({CqlSession.class, CassandraAdminOperations.class})
@ConditionalOnBean({CqlSession.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.class */
public class CassandraDataAutoConfiguration {
    private final CqlSession session;

    public CassandraDataAutoConfiguration(CqlSession session) {
        this.session = session;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraMappingContext cassandraMapping(BeanFactory beanFactory, CassandraCustomConversions conversions) throws ClassNotFoundException {
        CassandraMappingContext context = new CassandraMappingContext();
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        if (!packages.isEmpty()) {
            context.setInitialEntitySet(CassandraEntityClassScanner.scan(packages));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraConverter cassandraConverter(CassandraMappingContext mapping, CassandraCustomConversions conversions) {
        MappingCassandraConverter converter = new MappingCassandraConverter(mapping);
        converter.setCodecRegistry(this.session.getContext().getCodecRegistry());
        converter.setCustomConversions(conversions);
        converter.setUserTypeResolver(new SimpleUserTypeResolver(this.session));
        return converter;
    }

    @ConditionalOnMissingBean({SessionFactory.class})
    @Bean
    public SessionFactoryFactoryBean cassandraSessionFactory(Environment environment, CassandraConverter converter) {
        SessionFactoryFactoryBean session = new SessionFactoryFactoryBean();
        session.setSession(this.session);
        session.setConverter(converter);
        Binder binder = Binder.get(environment);
        BindResult bindResultBind = binder.bind("spring.data.cassandra.schema-action", SchemaAction.class);
        session.getClass();
        bindResultBind.ifBound(session::setSchemaAction);
        return session;
    }

    @ConditionalOnMissingBean({CassandraOperations.class})
    @Bean
    public CassandraTemplate cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        return new CassandraTemplate(sessionFactory, converter);
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraCustomConversions cassandraCustomConversions() {
        return new CassandraCustomConversions(Collections.emptyList());
    }
}
