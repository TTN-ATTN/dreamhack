package freemarker.core;

import freemarker.debug.impl.DebuggerService;
import freemarker.template.TemplateException;
import java.io.IOException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DebugBreak.class */
public class DebugBreak extends TemplateElement {
    public DebugBreak(TemplateElement nestedBlock) {
        addChild(nestedBlock);
        copyLocationFrom(nestedBlock);
    }

    @Override // freemarker.core.TemplateElement
    protected TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        if (!DebuggerService.suspendEnvironment(env, getTemplate().getSourceName(), getChild(0).getBeginLine())) {
            return getChild(0).accept(env);
        }
        throw new StopException(env, "Stopped by debugger");
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder sb = new StringBuilder();
            sb.append("<#-- ");
            sb.append("debug break");
            if (getChildCount() == 0) {
                sb.append(" /-->");
            } else {
                sb.append(" -->");
                sb.append(getChild(0).getCanonicalForm());
                sb.append("<#--/ debug break -->");
            }
            return sb.toString();
        }
        return "debug break";
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#debug_break";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
