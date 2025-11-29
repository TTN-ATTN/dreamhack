package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInForHashEx.class */
abstract class BuiltInForHashEx extends BuiltIn {
    abstract TemplateModel calculateResult(TemplateHashModelEx templateHashModelEx, Environment environment) throws TemplateModelException, InvalidReferenceException;

    BuiltInForHashEx() {
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateHashModelEx) {
            return calculateResult((TemplateHashModelEx) model, env);
        }
        throw new NonExtendedHashException(this.target, model, env);
    }

    protected InvalidReferenceException newNullPropertyException(String propertyName, TemplateModel tm, Environment env) {
        if (env.getFastInvalidReferenceExceptions()) {
            return InvalidReferenceException.FAST_INSTANCE;
        }
        return new InvalidReferenceException(new _ErrorDescriptionBuilder("The exteneded hash (of class ", tm.getClass().getName(), ") has returned null for its \"", propertyName, "\" property. This is maybe a bug. The extended hash was returned by this expression:").blame(this.target), env, this);
    }
}
