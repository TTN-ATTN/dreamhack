package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Expression.class */
public abstract class Expression extends TemplateObject {
    TemplateModel constantValue;

    abstract TemplateModel _eval(Environment environment) throws TemplateException;

    abstract boolean isLiteral();

    protected abstract Expression deepCloneWithIdentifierReplaced_inner(String str, Expression expression, ReplacemenetState replacemenetState);

    @Override // freemarker.core.TemplateObject
    final void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
        super.setLocation(template, beginColumn, beginLine, endColumn, endLine);
        if (isLiteral()) {
            try {
                this.constantValue = _eval(null);
            } catch (Exception e) {
            }
        }
    }

    @Deprecated
    public final TemplateModel getAsTemplateModel(Environment env) throws TemplateException {
        return eval(env);
    }

    void enableLazilyGeneratedResult() {
    }

    final TemplateModel eval(Environment env) throws TemplateException {
        try {
            return this.constantValue != null ? this.constantValue : _eval(env);
        } catch (FlowControlException | TemplateException e) {
            throw e;
        } catch (Exception e2) {
            if (env != null && EvalUtil.shouldWrapUncheckedException(e2, env)) {
                throw new _MiscTemplateException(this, e2, env, "Expression has thrown an unchecked exception; see the cause exception.");
            }
            if (e2 instanceof RuntimeException) {
                throw ((RuntimeException) e2);
            }
            throw new UndeclaredThrowableException(e2);
        }
    }

    String evalAndCoerceToPlainText(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToPlainText(eval(env), this, null, env);
    }

    String evalAndCoerceToPlainText(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToPlainText(eval(env), this, seqTip, env);
    }

    Object evalAndCoerceToStringOrMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(eval(env), this, null, env);
    }

    Object evalAndCoerceToStringOrMarkup(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(eval(env), this, seqTip, env);
    }

    String evalAndCoerceToStringOrUnsupportedMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrUnsupportedMarkup(eval(env), this, null, env);
    }

    String evalAndCoerceToStringOrUnsupportedMarkup(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToStringOrUnsupportedMarkup(eval(env), this, seqTip, env);
    }

    Number evalToNumber(Environment env) throws TemplateException {
        TemplateModel model = eval(env);
        return modelToNumber(model, env);
    }

    final Number modelToNumber(TemplateModel model, Environment env) throws TemplateException {
        if (model instanceof TemplateNumberModel) {
            return EvalUtil.modelToNumber((TemplateNumberModel) model, this);
        }
        throw new NonNumericalException(this, model, env);
    }

    boolean evalToBoolean(Environment env) throws TemplateException {
        return evalToBoolean(env, null);
    }

    boolean evalToBoolean(Configuration cfg) throws TemplateException {
        return evalToBoolean(null, cfg);
    }

    final TemplateModel evalToNonMissing(Environment env) throws TemplateException {
        TemplateModel result = eval(env);
        assertNonNull(result, env);
        return result;
    }

    private boolean evalToBoolean(Environment env, Configuration cfg) throws TemplateException {
        TemplateModel model = eval(env);
        return modelToBoolean(model, env, cfg);
    }

    final boolean modelToBoolean(TemplateModel model, Environment env) throws TemplateException {
        return modelToBoolean(model, env, null);
    }

    final boolean modelToBoolean(TemplateModel model, Configuration cfg) throws TemplateException {
        return modelToBoolean(model, null, cfg);
    }

    private boolean modelToBoolean(TemplateModel model, Environment env, Configuration cfg) throws TemplateException {
        if (model instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel) model).getAsBoolean();
        }
        if (env == null ? !cfg.isClassicCompatible() : !env.isClassicCompatible()) {
            throw new NonBooleanException(this, model, env);
        }
        return (model == null || isEmpty(model)) ? false : true;
    }

    final Expression deepCloneWithIdentifierReplaced(String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
        Expression clone = deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
        if (clone.beginLine == 0) {
            clone.copyLocationFrom(this);
        }
        return clone;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Expression$ReplacemenetState.class */
    static class ReplacemenetState {
        boolean replacementAlreadyInUse;

        ReplacemenetState() {
        }
    }

    static boolean isEmpty(TemplateModel model) throws TemplateModelException {
        if (model instanceof BeanModel) {
            return ((BeanModel) model).isEmpty();
        }
        if (model instanceof TemplateSequenceModel) {
            return ((TemplateSequenceModel) model).size() == 0;
        }
        if (model instanceof TemplateScalarModel) {
            String s = ((TemplateScalarModel) model).getAsString();
            return s == null || s.length() == 0;
        }
        if (model == null) {
            return true;
        }
        if (model instanceof TemplateMarkupOutputModel) {
            TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel) model;
            return mo.getOutputFormat().isEmpty(mo);
        }
        if (model instanceof TemplateCollectionModel) {
            return !((TemplateCollectionModel) model).iterator().hasNext();
        }
        if (model instanceof TemplateHashModel) {
            return ((TemplateHashModel) model).isEmpty();
        }
        if ((model instanceof TemplateNumberModel) || (model instanceof TemplateDateModel) || (model instanceof TemplateBooleanModel)) {
            return false;
        }
        return true;
    }

    final void assertNonNull(TemplateModel model, Environment env) throws InvalidReferenceException {
        if (model == null) {
            throw InvalidReferenceException.getInstance(this, env);
        }
    }
}
