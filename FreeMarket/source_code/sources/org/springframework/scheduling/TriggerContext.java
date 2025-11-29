package org.springframework.scheduling;

import java.time.Clock;
import java.util.Date;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/TriggerContext.class */
public interface TriggerContext {
    @Nullable
    Date lastScheduledExecutionTime();

    @Nullable
    Date lastActualExecutionTime();

    @Nullable
    Date lastCompletionTime();

    default Clock getClock() {
        return Clock.systemDefaultZone();
    }
}
