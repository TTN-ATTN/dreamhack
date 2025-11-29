package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/task/TaskExecutorBuilder.class */
public class TaskExecutorBuilder {
    private final Integer queueCapacity;
    private final Integer corePoolSize;
    private final Integer maxPoolSize;
    private final Boolean allowCoreThreadTimeOut;
    private final Duration keepAlive;
    private final Boolean awaitTermination;
    private final Duration awaitTerminationPeriod;
    private final String threadNamePrefix;
    private final TaskDecorator taskDecorator;
    private final Set<TaskExecutorCustomizer> customizers;

    public TaskExecutorBuilder() {
        this.queueCapacity = null;
        this.corePoolSize = null;
        this.maxPoolSize = null;
        this.allowCoreThreadTimeOut = null;
        this.keepAlive = null;
        this.awaitTermination = null;
        this.awaitTerminationPeriod = null;
        this.threadNamePrefix = null;
        this.taskDecorator = null;
        this.customizers = null;
    }

    private TaskExecutorBuilder(Integer queueCapacity, Integer corePoolSize, Integer maxPoolSize, Boolean allowCoreThreadTimeOut, Duration keepAlive, Boolean awaitTermination, Duration awaitTerminationPeriod, String threadNamePrefix, TaskDecorator taskDecorator, Set<TaskExecutorCustomizer> customizers) {
        this.queueCapacity = queueCapacity;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.keepAlive = keepAlive;
        this.awaitTermination = awaitTermination;
        this.awaitTerminationPeriod = awaitTerminationPeriod;
        this.threadNamePrefix = threadNamePrefix;
        this.taskDecorator = taskDecorator;
        this.customizers = customizers;
    }

    public TaskExecutorBuilder queueCapacity(int queueCapacity) {
        return new TaskExecutorBuilder(Integer.valueOf(queueCapacity), this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder corePoolSize(int corePoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, Integer.valueOf(corePoolSize), this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder maxPoolSize(int maxPoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, Integer.valueOf(maxPoolSize), this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, Boolean.valueOf(allowCoreThreadTimeOut), this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder keepAlive(Duration keepAlive) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder awaitTermination(boolean awaitTermination) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, Boolean.valueOf(awaitTermination), this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder awaitTerminationPeriod(Duration awaitTerminationPeriod) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder threadNamePrefix(String threadNamePrefix) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder customizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder customizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, append(null, customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, append(this.customizers, customizers));
    }

    public ThreadPoolTaskExecutor build() {
        return configure(new ThreadPoolTaskExecutor());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ThreadPoolTaskExecutor> T build(Class<T> cls) {
        return (T) configure((ThreadPoolTaskExecutor) BeanUtils.instantiateClass(cls));
    }

    public <T extends ThreadPoolTaskExecutor> T configure(T taskExecutor) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) this.queueCapacity);
        taskExecutor.getClass();
        sourceFrom.to((v1) -> {
            r1.setQueueCapacity(v1);
        });
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) this.corePoolSize);
        taskExecutor.getClass();
        sourceFrom2.to((v1) -> {
            r1.setCorePoolSize(v1);
        });
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) this.maxPoolSize);
        taskExecutor.getClass();
        sourceFrom3.to((v1) -> {
            r1.setMaxPoolSize(v1);
        });
        PropertyMapper.Source<Integer> sourceAsInt = map.from((PropertyMapper) this.keepAlive).asInt((v0) -> {
            return v0.getSeconds();
        });
        taskExecutor.getClass();
        sourceAsInt.to((v1) -> {
            r1.setKeepAliveSeconds(v1);
        });
        PropertyMapper.Source sourceFrom4 = map.from((PropertyMapper) this.allowCoreThreadTimeOut);
        taskExecutor.getClass();
        sourceFrom4.to((v1) -> {
            r1.setAllowCoreThreadTimeOut(v1);
        });
        PropertyMapper.Source sourceFrom5 = map.from((PropertyMapper) this.awaitTermination);
        taskExecutor.getClass();
        sourceFrom5.to((v1) -> {
            r1.setWaitForTasksToCompleteOnShutdown(v1);
        });
        PropertyMapper.Source sourceAs = map.from((PropertyMapper) this.awaitTerminationPeriod).as((v0) -> {
            return v0.toMillis();
        });
        taskExecutor.getClass();
        sourceAs.to((v1) -> {
            r1.setAwaitTerminationMillis(v1);
        });
        PropertyMapper.Source sourceWhenHasText = map.from((PropertyMapper) this.threadNamePrefix).whenHasText();
        taskExecutor.getClass();
        sourceWhenHasText.to(taskExecutor::setThreadNamePrefix);
        PropertyMapper.Source sourceFrom6 = map.from((PropertyMapper) this.taskDecorator);
        taskExecutor.getClass();
        sourceFrom6.to(taskExecutor::setTaskDecorator);
        if (!CollectionUtils.isEmpty(this.customizers)) {
            this.customizers.forEach(customizer -> {
                customizer.customize(taskExecutor);
            });
        }
        return taskExecutor;
    }

    private <T> Set<T> append(Set<T> set, Iterable<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        result.getClass();
        additions.forEach(result::add);
        return Collections.unmodifiableSet(result);
    }
}
