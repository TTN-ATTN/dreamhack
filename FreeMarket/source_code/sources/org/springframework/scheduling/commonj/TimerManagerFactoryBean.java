package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerManager;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.lang.Nullable;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/TimerManagerFactoryBean.class */
public class TimerManagerFactoryBean extends TimerManagerAccessor implements FactoryBean<TimerManager>, InitializingBean, DisposableBean, Lifecycle {

    @Nullable
    private ScheduledTimerListener[] scheduledTimerListeners;

    @Nullable
    private List<Timer> timers;

    public void setScheduledTimerListeners(ScheduledTimerListener[] scheduledTimerListeners) {
        this.scheduledTimerListeners = scheduledTimerListeners;
    }

    @Override // org.springframework.scheduling.commonj.TimerManagerAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        Timer timerSchedule;
        super.afterPropertiesSet();
        if (this.scheduledTimerListeners != null) {
            this.timers = new ArrayList(this.scheduledTimerListeners.length);
            TimerManager timerManager = obtainTimerManager();
            for (ScheduledTimerListener scheduledTask : this.scheduledTimerListeners) {
                if (scheduledTask.isOneTimeTask()) {
                    timerSchedule = timerManager.schedule(scheduledTask.getTimerListener(), scheduledTask.getDelay());
                } else if (scheduledTask.isFixedRate()) {
                    timerSchedule = timerManager.scheduleAtFixedRate(scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod());
                } else {
                    timerSchedule = timerManager.schedule(scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod());
                }
                Timer timer = timerSchedule;
                this.timers.add(timer);
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public TimerManager getObject() {
        return getTimerManager();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends TimerManager> getObjectType() {
        TimerManager timerManager = getTimerManager();
        return timerManager != null ? timerManager.getClass() : TimerManager.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.scheduling.commonj.TimerManagerAccessor, org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.timers != null) {
            for (Timer timer : this.timers) {
                try {
                    timer.cancel();
                } catch (Throwable ex) {
                    this.logger.debug("Could not cancel CommonJ Timer", ex);
                }
            }
            this.timers.clear();
        }
        super.destroy();
    }
}
