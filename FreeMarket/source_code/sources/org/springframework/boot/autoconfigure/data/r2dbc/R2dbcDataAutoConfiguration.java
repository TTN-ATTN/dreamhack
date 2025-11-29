package org.springframework.boot.autoconfigure.data.r2dbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.r2dbc.core.DatabaseClient;

@AutoConfiguration(after = {R2dbcAutoConfiguration.class})
@ConditionalOnClass({DatabaseClient.class, R2dbcEntityTemplate.class})
@ConditionalOnSingleCandidate(DatabaseClient.class)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/data/r2dbc/R2dbcDataAutoConfiguration.class */
public class R2dbcDataAutoConfiguration {
    private final DatabaseClient databaseClient;
    private final R2dbcDialect dialect;

    public R2dbcDataAutoConfiguration(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
        this.dialect = DialectResolver.getDialect(this.databaseClient.getConnectionFactory());
    }

    @ConditionalOnMissingBean
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(R2dbcConverter r2dbcConverter) {
        return new R2dbcEntityTemplate(this.databaseClient, this.dialect, r2dbcConverter);
    }

    @ConditionalOnMissingBean
    @Bean
    public R2dbcMappingContext r2dbcMappingContext(ObjectProvider<NamingStrategy> namingStrategy, R2dbcCustomConversions r2dbcCustomConversions) {
        R2dbcMappingContext relationalMappingContext = new R2dbcMappingContext(namingStrategy.getIfAvailable(() -> {
            return NamingStrategy.INSTANCE;
        }));
        relationalMappingContext.setSimpleTypeHolder(r2dbcCustomConversions.getSimpleTypeHolder());
        return relationalMappingContext;
    }

    @ConditionalOnMissingBean
    @Bean
    public MappingR2dbcConverter r2dbcConverter(R2dbcMappingContext mappingContext, R2dbcCustomConversions r2dbcCustomConversions) {
        return new MappingR2dbcConverter(mappingContext, r2dbcCustomConversions);
    }

    @ConditionalOnMissingBean
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = new ArrayList<>((Collection<? extends Object>) this.dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        return new R2dbcCustomConversions(CustomConversions.StoreConversions.of(this.dialect.getSimpleTypeHolder(), converters), Collections.emptyList());
    }
}
