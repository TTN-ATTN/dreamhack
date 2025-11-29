package org.springframework.scheduling;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/TaskScheduler.class */
public interface TaskScheduler {
    @Nullable
    ScheduledFuture<?> schedule(Runnable task, Trigger trigger);

    ScheduledFuture<?> schedule(Runnable task, Date startTime);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay);

    default Clock getClock() {
        return Clock.systemDefaultZone();
    }

    default ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return schedule(task, Date.from(startTime));
    }

    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return scheduleAtFixedRate(task, Date.from(startTime), period.toMillis());
    }

    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return scheduleAtFixedRate(task, period.toMillis());
    }

    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return scheduleWithFixedDelay(task, Date.from(startTime), delay.toMillis());
    }

    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return scheduleWithFixedDelay(task, delay.toMillis());
    }
}
