package org.springframework.web.context.request;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/request/AbstractRequestAttributes.class */
public abstract class AbstractRequestAttributes implements RequestAttributes {
    protected final Map<String, Runnable> requestDestructionCallbacks = new LinkedHashMap(8);
    private volatile boolean requestActive = true;

    protected abstract void updateAccessedSessionAttributes();

    public void requestCompleted() {
        executeRequestDestructionCallbacks();
        updateAccessedSessionAttributes();
        this.requestActive = false;
    }

    protected final boolean isRequestActive() {
        return this.requestActive;
    }

    protected final void registerRequestDestructionCallback(String name, Runnable callback) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(callback, "Callback must not be null");
        synchronized (this.requestDestructionCallbacks) {
            this.requestDestructionCallbacks.put(name, callback);
        }
    }

    protected final void removeRequestDestructionCallback(String name) {
        Assert.notNull(name, "Name must not be null");
        synchronized (this.requestDestructionCallbacks) {
            this.requestDestructionCallbacks.remove(name);
        }
    }

    private void executeRequestDestructionCallbacks() {
        synchronized (this.requestDestructionCallbacks) {
            for (Runnable runnable : this.requestDestructionCallbacks.values()) {
                runnable.run();
            }
            this.requestDestructionCallbacks.clear();
        }
    }
}
