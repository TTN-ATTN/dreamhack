package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.SmartLifecycle;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/context/WebServerGracefulShutdownLifecycle.class */
public final class WebServerGracefulShutdownLifecycle implements SmartLifecycle {
    public static final int SMART_LIFECYCLE_PHASE = Integer.MAX_VALUE;
    private final WebServer webServer;
    private volatile boolean running;

    public WebServerGracefulShutdownLifecycle(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        this.running = true;
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        throw new UnsupportedOperationException("Stop must not be invoked directly");
    }

    @Override // org.springframework.context.SmartLifecycle
    public void stop(Runnable callback) {
        this.running = false;
        this.webServer.shutDownGracefully(result -> {
            callback.run();
        });
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.running;
    }

    @Override // org.springframework.context.SmartLifecycle, org.springframework.context.Phased
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
