package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.logging.log4j.Level;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/status/StatusConsoleListener.class */
public class StatusConsoleListener implements StatusListener {
    private Level level;
    private String[] filters;
    private final PrintStream stream;

    public StatusConsoleListener(final Level level) {
        this(level, System.out);
    }

    public StatusConsoleListener(final Level level, final PrintStream stream) {
        this.level = Level.FATAL;
        if (stream == null) {
            throw new IllegalArgumentException("You must provide a stream to use for this listener.");
        }
        this.level = level;
        this.stream = stream;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public Level getStatusLevel() {
        return this.level;
    }

    @Override // org.apache.logging.log4j.status.StatusListener
    public void log(final StatusData data) {
        if (!filtered(data)) {
            this.stream.println(data.getFormattedStatus());
        }
    }

    public void setFilters(final String... filters) {
        this.filters = filters;
    }

    private boolean filtered(final StatusData data) {
        if (this.filters == null) {
            return false;
        }
        String caller = data.getStackTraceElement().getClassName();
        for (String filter : this.filters) {
            if (caller.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.stream != System.out && this.stream != System.err) {
            this.stream.close();
        }
    }
}
