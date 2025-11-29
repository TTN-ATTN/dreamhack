package org.springframework.boot.autoconfigure.graphql.data;

import graphql.GraphQL;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.GraphQlSource;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, QuerydslDataFetcher.class, ReactiveQuerydslPredicateExecutor.class})
@ConditionalOnBean({GraphQlSource.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlReactiveQuerydslAutoConfiguration.class */
public class GraphQlReactiveQuerydslAutoConfiguration {
    @Bean
    public GraphQlSourceBuilderCustomizer reactiveQuerydslRegistrar(ObjectProvider<ReactiveQuerydslPredicateExecutor<?>> reactiveExecutors) {
        return new GraphQlQuerydslSourceBuilderCustomizer(QuerydslDataFetcher::autoRegistrationConfigurer, (ObjectProvider) null, reactiveExecutors);
    }
}
