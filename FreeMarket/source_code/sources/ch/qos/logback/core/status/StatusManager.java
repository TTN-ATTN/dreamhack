package ch.qos.logback.core.status;

import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/status/StatusManager.class */
public interface StatusManager {
    void add(Status status);

    List<Status> getCopyOfStatusList();

    int getCount();

    boolean add(StatusListener statusListener);

    void remove(StatusListener statusListener);

    void clear();

    List<StatusListener> getCopyOfStatusListenerList();
}
