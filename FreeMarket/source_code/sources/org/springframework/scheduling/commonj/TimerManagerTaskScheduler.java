package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/TimerManagerTaskScheduler.class */
public class TimerManagerTaskScheduler extends TimerManagerAccessor implements TaskScheduler {

    @Nullable
    private volatile ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return new ReschedulingTimerListener(errorHandlingTask(task, true), trigger).schedule();
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(errorHandlingTask(task, false));
        Timer timer = obtainTimerManager().schedule(futureTask, startTime);
        futureTask.setTimer(timer);
        return futureTask;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(errorHandlingTask(task, true));
        Timer timer = obtainTimerManager().scheduleAtFixedRate(futureTask, startTime, period);
        futureTask.setTimer(timer);
        return futureTask;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(errorHandlingTask(task, true));
        Timer timer = obtainTimerManager().scheduleAtFixedRate(futureTask, 0L, period);
        futureTask.setTimer(timer);
        return futureTask;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(errorHandlingTask(task, true));
        Timer timer = obtainTimerManager().schedule(futureTask, startTime, delay);
        futureTask.setTimer(timer);
        return futureTask;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(errorHandlingTask(task, true));
        Timer timer = obtainTimerManager().schedule(futureTask, 0L, delay);
        futureTask.setTimer(timer);
        return futureTask;
    }

    private Runnable errorHandlingTask(Runnable delegate, boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler(delegate, this.errorHandler, isRepeatingTask);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/TimerManagerTaskScheduler$TimerScheduledFuture.class */
    private static class TimerScheduledFuture extends FutureTask<Object> implements TimerListener, ScheduledFuture<Object> {

        @Nullable
        protected transient Timer timer;
        protected transient boolean cancelled;

        public TimerScheduledFuture(Runnable runnable) {
            super(runnable, null);
            this.cancelled = false;
        }

        public void setTimer(Timer timer) {
            this.timer = timer;
        }

        public void timerExpired(Timer timer) {
            runAndReset();
        }

        @Override // java.util.concurrent.FutureTask, java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean result = super.cancel(mayInterruptIfRunning);
            if (this.timer != null) {
                this.timer.cancel();
            }
            this.cancelled = true;
            return result;
        }

        @Override // java.util.concurrent.Delayed
        public long getDelay(TimeUnit unit) {
            Assert.state(this.timer != null, "No Timer available");
            return unit.convert(this.timer.getScheduledExecutionTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override // java.lang.Comparable
        public int compareTo(Delayed other) {
            if (this == other) {
                return 0;
            }
            long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
            if (diff == 0) {
                return 0;
            }
            return diff < 0 ? -1 : 1;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/TimerManagerTaskScheduler$ReschedulingTimerListener.class */
    private class ReschedulingTimerListener extends TimerScheduledFuture {
        private final Trigger trigger;
        private final SimpleTriggerContext triggerContext;
        private volatile Date scheduledExecutionTime;

        public ReschedulingTimerListener(Runnable runnable, Trigger trigger) {
            super(runnable);
            this.triggerContext = new SimpleTriggerContext();
            this.scheduledExecutionTime = new Date();
            this.trigger = trigger;
        }

        @Nullable
        public ScheduledFuture<?> schedule() {
            Date nextExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (nextExecutionTime == null) {
                return null;
            }
            this.scheduledExecutionTime = nextExecutionTime;
            setTimer(TimerManagerTaskScheduler.this.obtainTimerManager().schedule(this, this.scheduledExecutionTime));
            return this;
        }

        @Override // org.springframework.scheduling.commonj.TimerManagerTaskScheduler.TimerScheduledFuture
        public void timerExpired(Timer timer) {
            Date actualExecutionTime = new Date();
            super.timerExpired(timer);
            Date completionTime = new Date();
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!this.cancelled) {
                schedule();
            }
        }
    }
}
