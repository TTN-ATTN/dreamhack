package org.springframework.core.task;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/task/TaskTimeoutException.class */
public class TaskTimeoutException extends TaskRejectedException {
    public TaskTimeoutException(String msg) {
        super(msg);
    }

    public TaskTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
