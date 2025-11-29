package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NewBI.class */
class NewBI extends BuiltIn {
    static Class<?> JYTHON_MODEL_CLASS;

    NewBI() {
    }

    static {
        try {
            JYTHON_MODEL_CLASS = Class.forName("freemarker.ext.jython.JythonModel");
        } catch (Throwable th) {
            JYTHON_MODEL_CLASS = null;
        }
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        return new ConstructorFunction(this.target.evalAndCoerceToPlainText(env), env, this.target.getTemplate());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NewBI$ConstructorFunction.class */
    class ConstructorFunction implements TemplateMethodModelEx {
        private final Class<?> cl;
        private final Environment env;

        public ConstructorFunction(String classname, Environment env, Template template) throws TemplateException {
            this.env = env;
            this.cl = env.getNewBuiltinClassResolver().resolve(classname, env, template);
            if (!TemplateModel.class.isAssignableFrom(this.cl)) {
                throw new _MiscTemplateException(NewBI.this, env, "Class ", this.cl.getName(), " does not implement freemarker.template.TemplateModel");
            }
            if (BeanModel.class.isAssignableFrom(this.cl)) {
                throw new _MiscTemplateException(NewBI.this, env, "Bean Models cannot be instantiated using the ?", NewBI.this.key, " built-in");
            }
            if (NewBI.JYTHON_MODEL_CLASS != null && NewBI.JYTHON_MODEL_CLASS.isAssignableFrom(this.cl)) {
                throw new _MiscTemplateException(NewBI.this, env, "Jython Models cannot be instantiated using the ?", NewBI.this.key, " built-in");
            }
        }

        @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
        public Object exec(List arguments) throws TemplateModelException {
            ObjectWrapper ow = this.env.getObjectWrapper();
            BeansWrapper bw = ow instanceof BeansWrapper ? (BeansWrapper) ow : BeansWrapper.getDefaultInstance();
            return bw.newInstance(this.cl, arguments);
        }
    }
}
