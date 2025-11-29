package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/pattern/PostCompileProcessor.class */
public interface PostCompileProcessor<E> {
    void process(Context context, Converter<E> converter);
}
