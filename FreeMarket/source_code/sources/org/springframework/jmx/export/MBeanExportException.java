package org.springframework.jmx.export;

import org.springframework.jmx.JmxException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jmx/export/MBeanExportException.class */
public class MBeanExportException extends JmxException {
    public MBeanExportException(String msg) {
        super(msg);
    }

    public MBeanExportException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
