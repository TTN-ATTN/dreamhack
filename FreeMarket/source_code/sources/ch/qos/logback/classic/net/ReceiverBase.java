package ch.qos.logback.classic.net;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/net/ReceiverBase.class */
public abstract class ReceiverBase extends ContextAwareBase implements LifeCycle {
    private boolean started;

    protected abstract boolean shouldStart();

    protected abstract void onStop();

    protected abstract Runnable getRunnableTask();

    @Override // ch.qos.logback.core.spi.LifeCycle
    public final void start() {
        if (isStarted()) {
            return;
        }
        if (getContext() == null) {
            throw new IllegalStateException("context not set");
        }
        if (shouldStart()) {
            getContext().getScheduledExecutorService().execute(getRunnableTask());
            this.started = true;
        }
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public final void stop() {
        if (!isStarted()) {
            return;
        }
        try {
            onStop();
        } catch (RuntimeException ex) {
            addError("on stop: " + ex, ex);
        }
        this.started = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public final boolean isStarted() {
        return this.started;
    }
}
