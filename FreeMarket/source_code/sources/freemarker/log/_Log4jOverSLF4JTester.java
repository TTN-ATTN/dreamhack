package freemarker.log;

import org.apache.log4j.MDC;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/_Log4jOverSLF4JTester.class */
public class _Log4jOverSLF4JTester {
    private static final String MDC_KEY = _Log4jOverSLF4JTester.class.getName();

    public static final boolean test() {
        MDC.put(MDC_KEY, "");
        try {
            boolean z = org.slf4j.MDC.get(MDC_KEY) != null;
            MDC.remove(MDC_KEY);
            return z;
        } catch (Throwable th) {
            MDC.remove(MDC_KEY);
            throw th;
        }
    }
}
