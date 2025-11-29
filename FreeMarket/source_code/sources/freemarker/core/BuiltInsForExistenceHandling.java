package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling.class */
class BuiltInsForExistenceHandling {
    private BuiltInsForExistenceHandling() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$ExistenceBuiltIn.class */
    private static abstract class ExistenceBuiltIn extends BuiltIn {
        private ExistenceBuiltIn() {
        }

        protected TemplateModel evalMaybeNonexistentTarget(Environment env) throws TemplateException {
            TemplateModel tm;
            if (this.target instanceof ParentheticalExpression) {
                boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
                try {
                    tm = this.target.eval(env);
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                } catch (InvalidReferenceException e) {
                    tm = null;
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                } catch (Throwable th) {
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                    throw th;
                }
            } else {
                tm = this.target.eval(env);
            }
            return tm;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$defaultBI.class */
    static class defaultBI extends ExistenceBuiltIn {
        private static final TemplateMethodModelEx FIRST_NON_NULL_METHOD = new TemplateMethodModelEx() { // from class: freemarker.core.BuiltInsForExistenceHandling.defaultBI.1
            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                int argCnt = args.size();
                if (argCnt == 0) {
                    throw _MessageUtil.newArgCntError("?default", argCnt, 1, Integer.MAX_VALUE);
                }
                for (int i = 0; i < argCnt; i++) {
                    TemplateModel result = (TemplateModel) args.get(i);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
        };

        defaultBI() {
            super();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = evalMaybeNonexistentTarget(env);
            return model == null ? FIRST_NON_NULL_METHOD : new ConstantMethod(model);
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$defaultBI$ConstantMethod.class */
        private static class ConstantMethod implements TemplateMethodModelEx {
            private final TemplateModel constant;

            ConstantMethod(TemplateModel constant) {
                this.constant = constant;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) {
                return this.constant;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$existsBI.class */
    static class existsBI extends ExistenceBuiltIn {
        existsBI() {
            super();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            return evalMaybeNonexistentTarget(env) == null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
        }

        @Override // freemarker.core.Expression
        boolean evalToBoolean(Environment env) throws TemplateException {
            return _eval(env) == TemplateBooleanModel.TRUE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$has_contentBI.class */
    static class has_contentBI extends ExistenceBuiltIn {
        has_contentBI() {
            super();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            return Expression.isEmpty(evalMaybeNonexistentTarget(env)) ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
        }

        @Override // freemarker.core.Expression
        boolean evalToBoolean(Environment env) throws TemplateException {
            return _eval(env) == TemplateBooleanModel.TRUE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForExistenceHandling$if_existsBI.class */
    static class if_existsBI extends ExistenceBuiltIn {
        if_existsBI() {
            super();
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = evalMaybeNonexistentTarget(env);
            return model == null ? TemplateModel.NOTHING : model;
        }
    }
}
