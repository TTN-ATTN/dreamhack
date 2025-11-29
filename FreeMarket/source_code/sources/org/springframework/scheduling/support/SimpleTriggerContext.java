package org.springframework.scheduling.support;

import java.time.Clock;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TriggerContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/support/SimpleTriggerContext.class */
public class SimpleTriggerContext implements TriggerContext {
    private final Clock clock;

    @Nullable
    private volatile Date lastScheduledExecutionTime;

    @Nullable
    private volatile Date lastActualExecutionTime;

    @Nullable
    private volatile Date lastCompletionTime;

    public SimpleTriggerContext() {
        this.clock = Clock.systemDefaultZone();
    }

    public SimpleTriggerContext(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this();
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    public SimpleTriggerContext(Clock clock) {
        this.clock = clock;
    }

    public void update(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    public Clock getClock() {
        return this.clock;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastScheduledExecutionTime() {
        return this.lastScheduledExecutionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastActualExecutionTime() {
        return this.lastActualExecutionTime;
    }

    @Override // org.springframework.scheduling.TriggerContext
    @Nullable
    public Date lastCompletionTime() {
        return this.lastCompletionTime;
    }
}
