package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/ParameterVisitable.class */
public interface ParameterVisitable {
    <S> void forEachParameter(ParameterConsumer<S> action, S state);
}
