package org.springframework.boot.autoconfigure.graphql.data;

import graphql.GraphQL;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.graphql.data.query.QueryByExampleDataFetcher;
import org.springframework.graphql.execution.GraphQlSource;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, QueryByExampleDataFetcher.class, QueryByExampleExecutor.class})
@ConditionalOnBean({GraphQlSource.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlQueryByExampleAutoConfiguration.class */
public class GraphQlQueryByExampleAutoConfiguration {
    @Bean
    public GraphQlSourceBuilderCustomizer queryByExampleRegistrar(ObjectProvider<QueryByExampleExecutor<?>> executors, ObjectProvider<ReactiveQueryByExampleExecutor<?>> reactiveExecutors) {
        return new GraphQlQuerydslSourceBuilderCustomizer(QueryByExampleDataFetcher::autoRegistrationConfigurer, executors, reactiveExecutors);
    }
}
