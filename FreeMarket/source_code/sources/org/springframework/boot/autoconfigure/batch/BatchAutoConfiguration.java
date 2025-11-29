package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({BatchProperties.class})
@AutoConfiguration(after = {HibernateJpaAutoConfiguration.class})
@ConditionalOnClass({JobLauncher.class, DataSource.class})
@ConditionalOnBean({JobLauncher.class})
@Import({BatchConfigurerConfiguration.class, DatabaseInitializationDependencyConfigurer.class})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.class */
public class BatchAutoConfiguration {
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = properties.getJob().getNames();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobNames(jobNames);
        }
        return runner;
    }

    @ConditionalOnMissingBean({ExitCodeGenerator.class})
    @Bean
    public JobExecutionExitCodeGenerator jobExecutionExitCodeGenerator() {
        return new JobExecutionExitCodeGenerator();
    }

    @ConditionalOnMissingBean({JobOperator.class})
    @Bean
    public SimpleJobOperator jobOperator(ObjectProvider<JobParametersConverter> jobParametersConverter, JobExplorer jobExplorer, JobLauncher jobLauncher, ListableJobLocator jobRegistry, JobRepository jobRepository) throws Exception {
        SimpleJobOperator factory = new SimpleJobOperator();
        factory.setJobExplorer(jobExplorer);
        factory.setJobLauncher(jobLauncher);
        factory.setJobRegistry(jobRegistry);
        factory.setJobRepository(jobRepository);
        factory.getClass();
        jobParametersConverter.ifAvailable(factory::setJobParametersConverter);
        return factory;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({DatabasePopulator.class})
    @ConditionalOnBean({DataSource.class})
    @Conditional({OnBatchDatasourceInitializationCondition.class})
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration$DataSourceInitializerConfiguration.class */
    static class DataSourceInitializerConfiguration {
        DataSourceInitializerConfiguration() {
        }

        @ConditionalOnMissingBean({BatchDataSourceScriptDatabaseInitializer.class, BatchDataSourceInitializer.class})
        @Bean
        BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource, BatchProperties properties) {
            return new BatchDataSourceScriptDatabaseInitializer(batchDataSource.getIfAvailable(() -> {
                return dataSource;
            }), properties.getJdbc());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration$OnBatchDatasourceInitializationCondition.class */
    static class OnBatchDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
        OnBatchDatasourceInitializationCondition() {
            super("Batch", "spring.batch.jdbc.initialize-schema", "spring.batch.initialize-schema");
        }
    }
}
