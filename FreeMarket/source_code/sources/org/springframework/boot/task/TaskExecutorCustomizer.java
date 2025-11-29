package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@FunctionalInterface
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/task/TaskExecutorCustomizer.class */
public interface TaskExecutorCustomizer {
    void customize(ThreadPoolTaskExecutor taskExecutor);
}
