package org.springframework.scheduling.commonj;

import commonj.timers.TimerManager;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/TimerManagerAccessor.class */
public abstract class TimerManagerAccessor extends JndiLocatorSupport implements InitializingBean, DisposableBean, Lifecycle {

    @Nullable
    private TimerManager timerManager;

    @Nullable
    private String timerManagerName;
    private boolean shared = false;

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    public void setTimerManagerName(String timerManagerName) {
        this.timerManagerName = timerManagerName;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        if (this.timerManager == null) {
            if (this.timerManagerName == null) {
                throw new IllegalArgumentException("Either 'timerManager' or 'timerManagerName' must be specified");
            }
            this.timerManager = (TimerManager) lookup(this.timerManagerName, TimerManager.class);
        }
    }

    @Nullable
    protected final TimerManager getTimerManager() {
        return this.timerManager;
    }

    protected TimerManager obtainTimerManager() {
        Assert.notNull(this.timerManager, "No TimerManager set");
        return this.timerManager;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        if (!this.shared) {
            obtainTimerManager().resume();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        if (!this.shared) {
            obtainTimerManager().suspend();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        TimerManager tm = obtainTimerManager();
        return (tm.isSuspending() || tm.isStopping()) ? false : true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.timerManager != null && !this.shared) {
            this.timerManager.stop();
        }
    }
}
