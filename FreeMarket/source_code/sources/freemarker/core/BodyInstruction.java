package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Environment.Namespace;
import freemarker.core.Macro;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BodyInstruction.class */
final class BodyInstruction extends TemplateElement {
    private List bodyParameters;

    BodyInstruction(List bodyParameters) {
        this.bodyParameters = bodyParameters;
    }

    List getBodyParameters() {
        return this.bodyParameters;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Context bodyContext = new Context(env);
        env.invokeNestedContent(bodyContext);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        if (this.bodyParameters != null) {
            for (int i = 0; i < this.bodyParameters.size(); i++) {
                sb.append(' ');
                sb.append(((Expression) this.bodyParameters.get(i)).getCanonicalForm());
            }
        }
        if (canonical) {
            sb.append('>');
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#nested";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        if (this.bodyParameters != null) {
            return this.bodyParameters.size();
        }
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        checkIndex(idx);
        return this.bodyParameters.get(idx);
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        checkIndex(idx);
        return ParameterRole.PASSED_VALUE;
    }

    private void checkIndex(int idx) {
        if (this.bodyParameters == null || idx >= this.bodyParameters.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BodyInstruction$Context.class */
    class Context implements LocalContext {
        Macro.Context invokingMacroContext;
        Environment.Namespace bodyVars;

        Context(Environment env) throws TemplateException {
            TemplateModel templateModel;
            this.invokingMacroContext = env.getCurrentMacroContext();
            List bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
            if (BodyInstruction.this.bodyParameters != null) {
                for (int i = 0; i < BodyInstruction.this.bodyParameters.size(); i++) {
                    Expression exp = (Expression) BodyInstruction.this.bodyParameters.get(i);
                    TemplateModel tm = exp.eval(env);
                    if (bodyParameterNames != null && i < bodyParameterNames.size()) {
                        String bodyParameterName = bodyParameterNames.get(i);
                        if (this.bodyVars == null) {
                            env.getClass();
                            this.bodyVars = env.new Namespace();
                        }
                        Environment.Namespace namespace = this.bodyVars;
                        if (tm != null) {
                            templateModel = tm;
                        } else {
                            templateModel = BodyInstruction.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE;
                        }
                        namespace.put(bodyParameterName, templateModel);
                    }
                }
            }
        }

        @Override // freemarker.core.LocalContext
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            if (this.bodyVars == null) {
                return null;
            }
            return this.bodyVars.get(name);
        }

        @Override // freemarker.core.LocalContext
        public Collection getLocalVariableNames() {
            List bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
            return bodyParameterNames == null ? Collections.EMPTY_LIST : bodyParameterNames;
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
