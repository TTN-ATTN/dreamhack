package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LibraryLoad.class */
public final class LibraryLoad extends TemplateElement {
    private Expression importedTemplateNameExp;
    private String targetNsVarName;

    LibraryLoad(Template template, Expression templateName, String targetNsVarName) {
        this.targetNsVarName = targetNsVarName;
        this.importedTemplateNameExp = templateName;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        String importedTemplateName = this.importedTemplateNameExp.evalAndCoerceToPlainText(env);
        try {
            String fullImportedTemplateName = env.toFullTemplateName(getTemplate().getName(), importedTemplateName);
            try {
                env.importLib(fullImportedTemplateName, this.targetNsVarName);
                return null;
            } catch (IOException e) {
                throw new _MiscTemplateException(e, env, "Template importing failed (for parameter value ", new _DelayedJQuote(importedTemplateName), "):\n", new _DelayedGetMessage(e));
            }
        } catch (MalformedTemplateNameException e2) {
            throw new _MiscTemplateException(e2, env, "Malformed template name ", new _DelayedJQuote(e2.getTemplateName()), ":\n", e2.getMalformednessDescription());
        }
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.importedTemplateNameExp.getCanonicalForm());
        buf.append(" as ");
        buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.targetNsVarName));
        if (canonical) {
            buf.append("/>");
        }
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#import";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.importedTemplateNameExp;
            case 1:
                return this.targetNsVarName;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.TEMPLATE_NAME;
            case 1:
                return ParameterRole.NAMESPACE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public String getTemplateName() {
        return this.importedTemplateNameExp.toString();
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
