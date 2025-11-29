package freemarker.template;

import freemarker.core.Environment;
import freemarker.log.Logger;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/LoggingAttemptExceptionReporter.class */
class LoggingAttemptExceptionReporter implements AttemptExceptionReporter {
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private final boolean logAsWarn;

    public LoggingAttemptExceptionReporter(boolean logAsWarn) {
        this.logAsWarn = logAsWarn;
    }

    @Override // freemarker.template.AttemptExceptionReporter
    public void report(TemplateException te, Environment env) {
        if (!this.logAsWarn) {
            LOG.error("Error executing FreeMarker template part in the #attempt block", te);
        } else {
            LOG.warn("Error executing FreeMarker template part in the #attempt block", te);
        }
    }
}
