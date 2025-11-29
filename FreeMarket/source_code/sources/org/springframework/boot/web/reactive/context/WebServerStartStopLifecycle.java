package org.springframework.boot.web.reactive.context;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.SmartLifecycle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/context/WebServerStartStopLifecycle.class */
class WebServerStartStopLifecycle implements SmartLifecycle {
    private final WebServerManager weServerManager;
    private volatile boolean running;

    WebServerStartStopLifecycle(WebServerManager weServerManager) {
        this.weServerManager = weServerManager;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() throws WebServerException {
        this.weServerManager.start();
        this.running = true;
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() throws WebServerException {
        this.running = false;
        this.weServerManager.stop();
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.running;
    }

    @Override // org.springframework.context.SmartLifecycle, org.springframework.context.Phased
    public int getPhase() {
        return 2147483646;
    }
}
