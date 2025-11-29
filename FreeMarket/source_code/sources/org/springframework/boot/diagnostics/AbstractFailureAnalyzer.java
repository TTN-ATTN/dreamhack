package org.springframework.boot.diagnostics;

import java.lang.Throwable;
import org.springframework.core.ResolvableType;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/diagnostics/AbstractFailureAnalyzer.class */
public abstract class AbstractFailureAnalyzer<T extends Throwable> implements FailureAnalyzer {
    protected abstract FailureAnalysis analyze(Throwable rootFailure, T cause);

    @Override // org.springframework.boot.diagnostics.FailureAnalyzer
    public FailureAnalysis analyze(Throwable th) {
        Throwable thFindCause = findCause(th, getCauseType());
        if (thFindCause != null) {
            return analyze(th, thFindCause);
        }
        return null;
    }

    protected Class<? extends T> getCauseType() {
        return (Class<? extends T>) ResolvableType.forClass(AbstractFailureAnalyzer.class, getClass()).resolveGeneric(new int[0]);
    }

    protected final <E extends Throwable> E findCause(Throwable th, Class<E> cls) {
        while (th != null) {
            if (cls.isInstance(th)) {
                return (E) th;
            }
            th = th.getCause();
        }
        return null;
    }
}
