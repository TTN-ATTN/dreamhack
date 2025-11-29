package freemarker.core;

import ch.qos.logback.classic.spi.CallerData;
import freemarker.core.Macro;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables.class */
class BuiltInsForCallables {
    BuiltInsForCallables() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$AbstractWithArgsBI.class */
    static abstract class AbstractWithArgsBI extends BuiltIn {
        protected abstract boolean isOrderLast();

        AbstractWithArgsBI() {
        }

        @Override // freemarker.core.Expression
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof Macro) {
                return new BIMethodForMacroAndFunction((Macro) model);
            }
            if (model instanceof TemplateDirectiveModel) {
                return new BIMethodForDirective((TemplateDirectiveModel) model);
            }
            if (model instanceof TemplateMethodModel) {
                return new BIMethodForMethod((TemplateMethodModel) model);
            }
            throw new UnexpectedTypeException(this.target, model, "macro, function, directive, or method", new Class[]{Macro.class, TemplateDirectiveModel.class, TemplateMethodModel.class}, env);
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$AbstractWithArgsBI$BIMethodForMacroAndFunction.class */
        private class BIMethodForMacroAndFunction implements TemplateMethodModelEx {
            private final Macro macroOrFunction;

            private BIMethodForMacroAndFunction(Macro macroOrFunction) {
                this.macroOrFunction = macroOrFunction;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                Macro.WithArgs withArgs;
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel) args.get(0);
                if (argTM instanceof TemplateSequenceModel) {
                    withArgs = new Macro.WithArgs((TemplateSequenceModel) argTM, AbstractWithArgsBI.this.isOrderLast());
                } else if (argTM instanceof TemplateHashModelEx) {
                    if (this.macroOrFunction.isFunction()) {
                        throw new _TemplateModelException("When applied on a function, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument.");
                    }
                    withArgs = new Macro.WithArgs((TemplateHashModelEx) argTM, AbstractWithArgsBI.this.isOrderLast());
                } else {
                    throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException(CallerData.NA + AbstractWithArgsBI.this.key, 0, argTM);
                }
                return new Macro(this.macroOrFunction, withArgs);
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$AbstractWithArgsBI$BIMethodForMethod.class */
        private class BIMethodForMethod implements TemplateMethodModelEx {
            private final TemplateMethodModel method;

            public BIMethodForMethod(TemplateMethodModel method) {
                this.method = method;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel) args.get(0);
                if (argTM instanceof TemplateSequenceModel) {
                    final TemplateSequenceModel withArgs = (TemplateSequenceModel) argTM;
                    if (this.method instanceof TemplateMethodModelEx) {
                        return new TemplateMethodModelEx() { // from class: freemarker.core.BuiltInsForCallables.AbstractWithArgsBI.BIMethodForMethod.1
                            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
                            public Object exec(List origArgs) throws TemplateModelException {
                                int withArgsSize = withArgs.size();
                                List<TemplateModel> newArgs = new ArrayList<>(withArgsSize + origArgs.size());
                                if (AbstractWithArgsBI.this.isOrderLast()) {
                                    newArgs.addAll(origArgs);
                                }
                                for (int i = 0; i < withArgsSize; i++) {
                                    newArgs.add(withArgs.get(i));
                                }
                                if (!AbstractWithArgsBI.this.isOrderLast()) {
                                    newArgs.addAll(origArgs);
                                }
                                return BIMethodForMethod.this.method.exec(newArgs);
                            }
                        };
                    }
                    return new TemplateMethodModel() { // from class: freemarker.core.BuiltInsForCallables.AbstractWithArgsBI.BIMethodForMethod.2
                        @Override // freemarker.template.TemplateMethodModel
                        public Object exec(List origArgs) throws TemplateModelException {
                            int withArgsSize = withArgs.size();
                            List<String> newArgs = new ArrayList<>(withArgsSize + origArgs.size());
                            if (AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.addAll(origArgs);
                            }
                            for (int i = 0; i < withArgsSize; i++) {
                                TemplateModel argVal = withArgs.get(i);
                                newArgs.add(argValueToString(argVal));
                            }
                            if (!AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.addAll(origArgs);
                            }
                            return BIMethodForMethod.this.method.exec(newArgs);
                        }

                        private String argValueToString(TemplateModel argVal) throws TemplateModelException {
                            String argValStr;
                            if (argVal instanceof TemplateScalarModel) {
                                argValStr = ((TemplateScalarModel) argVal).getAsString();
                            } else if (argVal == null) {
                                argValStr = null;
                            } else {
                                try {
                                    argValStr = EvalUtil.coerceModelToPlainText(argVal, null, null, Environment.getCurrentEnvironment());
                                } catch (TemplateException e) {
                                    throw new _TemplateModelException(e, "Failed to convert method argument to string. Argument type was: ", new _DelayedFTLTypeDescription(argVal));
                                }
                            }
                            return argValStr;
                        }
                    };
                }
                if (argTM instanceof TemplateHashModelEx) {
                    throw new _TemplateModelException("When applied on a method, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument.");
                }
                throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException(CallerData.NA + AbstractWithArgsBI.this.key, 0, argTM);
            }
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$AbstractWithArgsBI$BIMethodForDirective.class */
        private class BIMethodForDirective implements TemplateMethodModelEx {
            private final TemplateDirectiveModel directive;

            public BIMethodForDirective(TemplateDirectiveModel directive) {
                this.directive = directive;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel) args.get(0);
                if (argTM instanceof TemplateHashModelEx) {
                    final TemplateHashModelEx withArgs = (TemplateHashModelEx) argTM;
                    return new TemplateDirectiveModel() { // from class: freemarker.core.BuiltInsForCallables.AbstractWithArgsBI.BIMethodForDirective.1
                        @Override // freemarker.template.TemplateDirectiveModel
                        public void execute(Environment env, Map origArgs, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                            int withArgsSize = withArgs.size();
                            Map<String, TemplateModel> newArgs = new LinkedHashMap<>(((withArgsSize + origArgs.size()) * 4) / 3, 1.0f);
                            TemplateHashModelEx2.KeyValuePairIterator withArgsIter = TemplateModelUtils.getKeyValuePairIterator(withArgs);
                            if (AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.putAll(origArgs);
                                while (withArgsIter.hasNext()) {
                                    TemplateHashModelEx2.KeyValuePair withArgsKVP = withArgsIter.next();
                                    String argName = getArgumentName(withArgsKVP);
                                    if (!newArgs.containsKey(argName)) {
                                        newArgs.put(argName, withArgsKVP.getValue());
                                    }
                                }
                            } else {
                                while (withArgsIter.hasNext()) {
                                    TemplateHashModelEx2.KeyValuePair withArgsKVP2 = withArgsIter.next();
                                    newArgs.put(getArgumentName(withArgsKVP2), withArgsKVP2.getValue());
                                }
                                newArgs.putAll(origArgs);
                            }
                            BIMethodForDirective.this.directive.execute(env, newArgs, loopVars, body);
                        }

                        private String getArgumentName(TemplateHashModelEx2.KeyValuePair withArgsKVP) throws TemplateModelException {
                            TemplateModel argNameTM = withArgsKVP.getKey();
                            if (!(argNameTM instanceof TemplateScalarModel)) {
                                throw new _TemplateModelException("Expected string keys in the ?", AbstractWithArgsBI.this.key, "(...) arguments, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), ".");
                            }
                            return EvalUtil.modelToString((TemplateScalarModel) argNameTM, null, null);
                        }
                    };
                }
                if (argTM instanceof TemplateSequenceModel) {
                    throw new _TemplateModelException("When applied on a directive, ?", AbstractWithArgsBI.this.key, "(...) can't have a sequence argument. Use a hash argument.");
                }
                throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException(CallerData.NA + AbstractWithArgsBI.this.key, 0, argTM);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$with_argsBI.class */
    static final class with_argsBI extends AbstractWithArgsBI {
        with_argsBI() {
        }

        @Override // freemarker.core.BuiltInsForCallables.AbstractWithArgsBI
        protected boolean isOrderLast() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForCallables$with_args_lastBI.class */
    static final class with_args_lastBI extends AbstractWithArgsBI {
        with_args_lastBI() {
        }

        @Override // freemarker.core.BuiltInsForCallables.AbstractWithArgsBI
        protected boolean isOrderLast() {
            return true;
        }
    }
}
