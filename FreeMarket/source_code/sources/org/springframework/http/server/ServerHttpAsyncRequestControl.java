package org.springframework.http.server;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/ServerHttpAsyncRequestControl.class */
public interface ServerHttpAsyncRequestControl {
    void start();

    void start(long timeout);

    boolean isStarted();

    void complete();

    boolean isCompleted();
}
