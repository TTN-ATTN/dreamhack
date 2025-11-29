package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ThreadInterruptionSupportTemplatePostProcessor;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NestedContentNotSupportedException.class */
class NestedContentNotSupportedException extends TemplateException {
    public static void check(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
        if (body == null) {
            return;
        }
        if (body instanceof Environment.NestedElementTemplateDirectiveBody) {
            TemplateElement[] tes = ((Environment.NestedElementTemplateDirectiveBody) body).getChildrenBuffer();
            if (tes == null || tes.length == 0) {
                return;
            }
            if ((tes[0] instanceof ThreadInterruptionSupportTemplatePostProcessor.ThreadInterruptionCheck) && (tes.length == 1 || tes[1] == null)) {
                return;
            }
        }
        throw new NestedContentNotSupportedException(Environment.getCurrentEnvironment());
    }

    private NestedContentNotSupportedException(Environment env) {
        this(null, null, env);
    }

    private NestedContentNotSupportedException(Exception cause, Environment env) {
        this(null, cause, env);
    }

    private NestedContentNotSupportedException(String description, Environment env) {
        this(description, null, env);
    }

    private NestedContentNotSupportedException(String description, Exception cause, Environment env) {
        super("Nested content (body) not supported." + (description != null ? " " + StringUtil.jQuote(description) : ""), cause, env);
    }
}
