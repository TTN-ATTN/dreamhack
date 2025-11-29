package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.metrics.ApplicationStartup;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/ApplicationStartupAware.class */
public interface ApplicationStartupAware extends Aware {
    void setApplicationStartup(ApplicationStartup applicationStartup);
}
