package org.springframework.boot.autoconfigure.graphql.data;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlQuerydslSourceBuilderCustomizer.class */
class GraphQlQuerydslSourceBuilderCustomizer<E, R> implements GraphQlSourceBuilderCustomizer {
    private final BiFunction<List<E>, List<R>, RuntimeWiringConfigurer> wiringConfigurerFactory;
    private final List<E> executors;
    private final List<R> reactiveExecutors;

    GraphQlQuerydslSourceBuilderCustomizer(BiFunction<List<E>, List<R>, RuntimeWiringConfigurer> wiringConfigurerFactory, ObjectProvider<E> executors, ObjectProvider<R> reactiveExecutors) {
        this(wiringConfigurerFactory, toList(executors), toList(reactiveExecutors));
    }

    GraphQlQuerydslSourceBuilderCustomizer(BiFunction<List<E>, List<R>, RuntimeWiringConfigurer> wiringConfigurerFactory, List<E> executors, List<R> reactiveExecutors) {
        this.wiringConfigurerFactory = wiringConfigurerFactory;
        this.executors = executors;
        this.reactiveExecutors = reactiveExecutors;
    }

    @Override // org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer
    public void customize(GraphQlSource.SchemaResourceBuilder builder) {
        if (!this.executors.isEmpty() || !this.reactiveExecutors.isEmpty()) {
            builder.configureRuntimeWiring(this.wiringConfigurerFactory.apply(this.executors, this.reactiveExecutors));
        }
    }

    private static <T> List<T> toList(ObjectProvider<T> provider) {
        return provider != null ? (List) provider.orderedStream().collect(Collectors.toList()) : Collections.emptyList();
    }
}
