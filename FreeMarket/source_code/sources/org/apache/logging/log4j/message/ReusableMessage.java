package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@PerformanceSensitive({"allocation"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/ReusableMessage.class */
public interface ReusableMessage extends Message, StringBuilderFormattable {
    Object[] swapParameters(Object[] emptyReplacement);

    short getParameterCount();

    Message memento();
}
