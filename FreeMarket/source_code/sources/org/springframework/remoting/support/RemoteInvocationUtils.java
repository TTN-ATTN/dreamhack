package org.springframework.remoting.support;

import java.util.HashSet;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/support/RemoteInvocationUtils.class */
public abstract class RemoteInvocationUtils {
    public static void fillInClientStackTraceIfPossible(Throwable ex) {
        if (ex != null) {
            StackTraceElement[] clientStack = new Throwable().getStackTrace();
            Set<Throwable> visitedExceptions = new HashSet<>();
            Throwable cause = ex;
            while (true) {
                Throwable exToUpdate = cause;
                if (exToUpdate != null && !visitedExceptions.contains(exToUpdate)) {
                    StackTraceElement[] serverStack = exToUpdate.getStackTrace();
                    StackTraceElement[] combinedStack = new StackTraceElement[serverStack.length + clientStack.length];
                    System.arraycopy(serverStack, 0, combinedStack, 0, serverStack.length);
                    System.arraycopy(clientStack, 0, combinedStack, serverStack.length, clientStack.length);
                    exToUpdate.setStackTrace(combinedStack);
                    visitedExceptions.add(exToUpdate);
                    cause = exToUpdate.getCause();
                } else {
                    return;
                }
            }
        }
    }
}
