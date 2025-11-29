package org.apache.el.util;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:org/apache/el/util/ExceptionUtils.class */
public class ExceptionUtils {
    public static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (!(t instanceof StackOverflowError) && (t instanceof VirtualMachineError)) {
            throw ((VirtualMachineError) t);
        }
    }

    public static void preload() {
    }
}
