package freemarker.core;

import freemarker.template.TemplateException;
import java.io.PrintStream;
import java.io.PrintWriter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/StopException.class */
public class StopException extends TemplateException {
    StopException(Environment env) {
        super(env);
    }

    StopException(Environment env, String s) {
        super(s, env);
    }

    @Override // freemarker.template.TemplateException, java.lang.Throwable
    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            String msg = getMessage();
            pw.print("Encountered stop instruction");
            if (msg != null && !msg.equals("")) {
                pw.println("\nCause given: " + msg);
            } else {
                pw.println();
            }
            super.printStackTrace(pw);
        }
    }

    @Override // freemarker.template.TemplateException, java.lang.Throwable
    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            String msg = getMessage();
            ps.print("Encountered stop instruction");
            if (msg != null && !msg.equals("")) {
                ps.println("\nCause given: " + msg);
            } else {
                ps.println();
            }
            super.printStackTrace(ps);
        }
    }
}
