package freemarker.template;

import freemarker.core.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/AttemptExceptionReporter.class */
public interface AttemptExceptionReporter {
    public static final AttemptExceptionReporter LOG_ERROR_REPORTER = new LoggingAttemptExceptionReporter(false);
    public static final AttemptExceptionReporter LOG_WARN_REPORTER = new LoggingAttemptExceptionReporter(true);

    void report(TemplateException templateException, Environment environment);
}
