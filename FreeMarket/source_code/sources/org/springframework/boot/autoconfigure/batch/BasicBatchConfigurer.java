package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/BasicBatchConfigurer.class */
public class BasicBatchConfigurer implements BatchConfigurer, InitializingBean {
    private final BatchProperties properties;
    private final DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private final TransactionManagerCustomizers transactionManagerCustomizers;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    protected BasicBatchConfigurer(BatchProperties properties, DataSource dataSource, TransactionManagerCustomizers transactionManagerCustomizers) {
        this.properties = properties;
        this.dataSource = dataSource;
        this.transactionManagerCustomizers = transactionManagerCustomizers;
    }

    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JobLauncher getJobLauncher() {
        return this.jobLauncher;
    }

    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initialize();
    }

    public void initialize() {
        try {
            this.transactionManager = buildTransactionManager();
            this.jobRepository = createJobRepository();
            this.jobLauncher = createJobLauncher();
            this.jobExplorer = createJobExplorer();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize Spring Batch", ex);
        }
    }

    protected JobExplorer createJobExplorer() throws Exception {
        PropertyMapper map = PropertyMapper.get();
        JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(this.dataSource);
        BatchProperties.Jdbc jdbc = this.properties.getJdbc();
        jdbc.getClass();
        PropertyMapper.Source sourceWhenHasText = map.from(jdbc::getTablePrefix).whenHasText();
        factory.getClass();
        sourceWhenHasText.to(factory::setTablePrefix);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        PropertyMapper map = PropertyMapper.get();
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) this.dataSource);
        factory.getClass();
        sourceFrom.to(factory::setDataSource);
        PropertyMapper.Source sourceWhenNonNull = map.from(this::determineIsolationLevel).whenNonNull();
        factory.getClass();
        sourceWhenNonNull.to(factory::setIsolationLevelForCreate);
        BatchProperties.Jdbc jdbc = this.properties.getJdbc();
        jdbc.getClass();
        PropertyMapper.Source sourceWhenHasText = map.from(jdbc::getTablePrefix).whenHasText();
        factory.getClass();
        sourceWhenHasText.to(factory::setTablePrefix);
        PropertyMapper.Source sourceFrom2 = map.from(this::getTransactionManager);
        factory.getClass();
        sourceFrom2.to(factory::setTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    protected String determineIsolationLevel() {
        BatchProperties.Isolation isolation = this.properties.getJdbc().getIsolationLevelForCreate();
        if (isolation != null) {
            return isolation.toIsolationName();
        }
        return null;
    }

    protected PlatformTransactionManager createTransactionManager() {
        return new DataSourceTransactionManager(this.dataSource);
    }

    private PlatformTransactionManager buildTransactionManager() {
        PlatformTransactionManager transactionManager = createTransactionManager();
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(transactionManager);
        }
        return transactionManager;
    }
}
