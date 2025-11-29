package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/DelegatingTimerListener.class */
public class DelegatingTimerListener implements TimerListener {
    private final Runnable runnable;

    public DelegatingTimerListener(Runnable runnable) {
        Assert.notNull(runnable, "Runnable is required");
        this.runnable = runnable;
    }

    public void timerExpired(Timer timer) {
        this.runnable.run();
    }
}
