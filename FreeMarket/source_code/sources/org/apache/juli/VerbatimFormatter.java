package org.apache.juli;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/juli/VerbatimFormatter.class */
public class VerbatimFormatter extends Formatter {
    @Override // java.util.logging.Formatter
    public String format(LogRecord record) {
        return record.getMessage() + System.lineSeparator();
    }
}
