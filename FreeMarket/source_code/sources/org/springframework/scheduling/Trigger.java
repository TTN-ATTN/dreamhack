package org.springframework.scheduling;

import java.util.Date;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/Trigger.class */
public interface Trigger {
    @Nullable
    Date nextExecutionTime(TriggerContext triggerContext);
}
