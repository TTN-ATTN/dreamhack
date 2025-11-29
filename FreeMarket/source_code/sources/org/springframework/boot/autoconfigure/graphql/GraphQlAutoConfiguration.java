package org.springframework.boot.autoconfigure.graphql;

import graphql.GraphQL;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.log.LogMessage;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry;
import org.springframework.graphql.execution.DefaultExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.execution.SubscriptionExceptionResolver;

@EnableConfigurationProperties({GraphQlProperties.class})
@AutoConfiguration
@ConditionalOnClass({GraphQL.class, GraphQlSource.class})
@ConditionalOnGraphQlSchema
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/GraphQlAutoConfiguration.class */
public class GraphQlAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) GraphQlAutoConfiguration.class);
    private final ListableBeanFactory beanFactory;

    public GraphQlAutoConfiguration(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @ConditionalOnMissingBean
    @Bean
    public GraphQlSource graphQlSource(ResourcePatternResolver resourcePatternResolver, GraphQlProperties properties, ObjectProvider<DataFetcherExceptionResolver> exceptionResolvers, ObjectProvider<SubscriptionExceptionResolver> subscriptionExceptionResolvers, ObjectProvider<Instrumentation> instrumentations, ObjectProvider<RuntimeWiringConfigurer> wiringConfigurers, ObjectProvider<GraphQlSourceBuilderCustomizer> sourceCustomizers) {
        String[] schemaLocations = properties.getSchema().getLocations();
        Resource[] schemaResources = resolveSchemaResources(resourcePatternResolver, schemaLocations, properties.getSchema().getFileExtensions());
        GraphQlSource.SchemaResourceBuilder builder = GraphQlSource.schemaResourceBuilder().schemaResources(schemaResources).exceptionResolvers(toList(exceptionResolvers)).subscriptionExceptionResolvers(toList(subscriptionExceptionResolvers)).instrumentation(toList(instrumentations));
        if (!properties.getSchema().getIntrospection().isEnabled()) {
            builder.configureRuntimeWiring(this::enableIntrospection);
        }
        Stream<RuntimeWiringConfigurer> streamOrderedStream = wiringConfigurers.orderedStream();
        builder.getClass();
        streamOrderedStream.forEach(builder::configureRuntimeWiring);
        sourceCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private RuntimeWiring.Builder enableIntrospection(RuntimeWiring.Builder wiring) {
        return wiring.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY);
    }

    private Resource[] resolveSchemaResources(ResourcePatternResolver resolver, String[] locations, String[] extensions) {
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            for (String extension : extensions) {
                resources.addAll(resolveSchemaResources(resolver, location + "*" + extension));
            }
        }
        return (Resource[]) resources.toArray(new Resource[0]);
    }

    private List<Resource> resolveSchemaResources(ResourcePatternResolver resolver, String pattern) {
        try {
            return Arrays.asList(resolver.getResources(pattern));
        } catch (IOException ex) {
            logger.debug(LogMessage.format("Could not resolve schema location: '%s'", pattern), ex);
            return Collections.emptyList();
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public BatchLoaderRegistry batchLoaderRegistry() {
        return new DefaultBatchLoaderRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutionGraphQlService executionGraphQlService(GraphQlSource graphQlSource, BatchLoaderRegistry batchLoaderRegistry) {
        DefaultExecutionGraphQlService service = new DefaultExecutionGraphQlService(graphQlSource);
        service.addDataLoaderRegistrar(batchLoaderRegistry);
        return service;
    }

    @ConditionalOnMissingBean
    @Bean
    public AnnotatedControllerConfigurer annotatedControllerConfigurer() {
        AnnotatedControllerConfigurer controllerConfigurer = new AnnotatedControllerConfigurer();
        controllerConfigurer.addFormatterRegistrar(registry -> {
            ApplicationConversionService.addBeans(registry, this.beanFactory);
        });
        return controllerConfigurer;
    }

    private <T> List<T> toList(ObjectProvider<T> provider) {
        return (List) provider.orderedStream().collect(Collectors.toList());
    }
}
