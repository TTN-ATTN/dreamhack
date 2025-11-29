package org.springframework.boot.autoconfigure.task;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.scheduling")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingProperties.class */
public class TaskSchedulingProperties {
    private final Pool pool = new Pool();
    private final Shutdown shutdown = new Shutdown();
    private String threadNamePrefix = "scheduling-";

    public Pool getPool() {
        return this.pool;
    }

    public Shutdown getShutdown() {
        return this.shutdown;
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingProperties$Pool.class */
    public static class Pool {
        private int size = 1;

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingProperties$Shutdown.class */
    public static class Shutdown {
        private boolean awaitTermination;
        private Duration awaitTerminationPeriod;

        public boolean isAwaitTermination() {
            return this.awaitTermination;
        }

        public void setAwaitTermination(boolean awaitTermination) {
            this.awaitTermination = awaitTermination;
        }

        public Duration getAwaitTerminationPeriod() {
            return this.awaitTerminationPeriod;
        }

        public void setAwaitTerminationPeriod(Duration awaitTerminationPeriod) {
            this.awaitTerminationPeriod = awaitTerminationPeriod;
        }
    }
}
