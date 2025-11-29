package ch.qos.logback.core.net.server;

import java.io.Closeable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/net/server/Client.class */
public interface Client extends Runnable, Closeable {
    void close();
}
