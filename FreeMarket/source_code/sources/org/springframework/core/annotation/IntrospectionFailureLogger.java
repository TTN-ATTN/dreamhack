package org.springframework.core.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/annotation/IntrospectionFailureLogger.class */
enum IntrospectionFailureLogger {
    DEBUG { // from class: org.springframework.core.annotation.IntrospectionFailureLogger.1
        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public boolean isEnabled() {
            return IntrospectionFailureLogger.getLogger().isDebugEnabled();
        }

        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public void log(String message) {
            IntrospectionFailureLogger.getLogger().debug(message);
        }
    },
    INFO { // from class: org.springframework.core.annotation.IntrospectionFailureLogger.2
        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public boolean isEnabled() {
            return IntrospectionFailureLogger.getLogger().isInfoEnabled();
        }

        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public void log(String message) {
            IntrospectionFailureLogger.getLogger().info(message);
        }
    };


    @Nullable
    private static Log logger;

    abstract boolean isEnabled();

    abstract void log(String message);

    void log(String message, @Nullable Object source, Exception ex) {
        String on = source != null ? " on " + source : "";
        log(message + on + ": " + ex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Log getLogger() {
        Log logger2 = logger;
        if (logger2 == null) {
            logger2 = LogFactory.getLog((Class<?>) MergedAnnotation.class);
            logger = logger2;
        }
        return logger2;
    }
}
