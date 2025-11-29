package org.springframework.scheduling.quartz;

import org.springframework.core.NestedRuntimeException;
import org.springframework.util.MethodInvoker;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/JobMethodInvocationFailedException.class */
public class JobMethodInvocationFailedException extends NestedRuntimeException {
    public JobMethodInvocationFailedException(MethodInvoker methodInvoker, Throwable cause) {
        super("Invocation of method '" + methodInvoker.getTargetMethod() + "' on target class [" + methodInvoker.getTargetClass() + "] failed", cause);
    }
}
