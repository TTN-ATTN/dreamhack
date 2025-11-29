package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.MultiformatMessage;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/MultiFormatStringBuilderFormattable.class */
public interface MultiFormatStringBuilderFormattable extends MultiformatMessage, StringBuilderFormattable {
    void formatTo(String[] formats, StringBuilder buffer);
}
