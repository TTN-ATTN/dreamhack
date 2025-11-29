package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Environment.Namespace;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.Constants;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Macro.class */
public final class Macro extends TemplateElement implements TemplateModel {
    static final Macro DO_NOTHING_MACRO = new Macro(".pass", Collections.EMPTY_MAP, null, false, false, TemplateElements.EMPTY);
    static final int TYPE_MACRO = 0;
    static final int TYPE_FUNCTION = 1;
    private final String name;
    private final String[] paramNames;
    private final Map<String, Expression> paramNamesWithDefault;
    private final WithArgs withArgs;
    private boolean requireArgsSpecialVariable;
    private final String catchAllParamName;
    private final boolean function;
    private final Object namespaceLookupKey;

    Macro(String name, Map<String, Expression> paramNamesWithDefault, String catchAllParamName, boolean function, boolean requireArgsSpecialVariable, TemplateElements children) {
        this.name = name;
        this.paramNamesWithDefault = paramNamesWithDefault;
        this.paramNames = (String[]) paramNamesWithDefault.keySet().toArray(new String[0]);
        this.catchAllParamName = catchAllParamName;
        this.withArgs = null;
        this.requireArgsSpecialVariable = requireArgsSpecialVariable;
        this.function = function;
        setChildren(children);
        this.namespaceLookupKey = this;
    }

    Macro(Macro that, WithArgs withArgs) {
        this.name = that.name;
        this.paramNamesWithDefault = that.paramNamesWithDefault;
        this.paramNames = that.paramNames;
        this.catchAllParamName = that.catchAllParamName;
        this.withArgs = withArgs;
        this.requireArgsSpecialVariable = that.requireArgsSpecialVariable;
        this.function = that.function;
        this.namespaceLookupKey = that.namespaceLookupKey;
        super.copyFieldsFrom((TemplateElement) that);
    }

    boolean getRequireArgsSpecialVariable() {
        return this.requireArgsSpecialVariable;
    }

    public String getCatchAll() {
        return this.catchAllParamName;
    }

    public String[] getArgumentNames() {
        return (String[]) this.paramNames.clone();
    }

    String[] getArgumentNamesNoCopy() {
        return this.paramNames;
    }

    public boolean hasArgNamed(String name) {
        return this.paramNamesWithDefault.containsKey(name);
    }

    public String getName() {
        return this.name;
    }

    public WithArgs getWithArgs() {
        return this.withArgs;
    }

    public Object getNamespaceLookupKey() {
        return this.namespaceLookupKey;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) {
        env.visitMacroDef(this);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        if (this.withArgs != null) {
            sb.append('?').append(getTemplate().getActualNamingConvention() == 12 ? "withArgs" : "with_args").append("(...)");
        }
        sb.append(' ');
        sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.name));
        if (this.function) {
            sb.append('(');
        }
        int argCnt = this.paramNames.length;
        for (int i = 0; i < argCnt; i++) {
            if (this.function) {
                if (i != 0) {
                    sb.append(", ");
                }
            } else {
                sb.append(' ');
            }
            String paramName = this.paramNames[i];
            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(paramName));
            Expression paramDefaultExp = this.paramNamesWithDefault.get(paramName);
            if (paramDefaultExp != null) {
                sb.append('=');
                if (this.function) {
                    sb.append(paramDefaultExp.getCanonicalForm());
                } else {
                    _MessageUtil.appendExpressionAsUntearable(sb, paramDefaultExp);
                }
            }
        }
        if (this.catchAllParamName != null) {
            if (this.function) {
                if (argCnt != 0) {
                    sb.append(", ");
                }
            } else {
                sb.append(' ');
            }
            sb.append(this.catchAllParamName);
            sb.append("...");
        }
        if (this.function) {
            sb.append(')');
        }
        if (canonical) {
            sb.append('>');
            sb.append(getChildrenCanonicalForm());
            sb.append("</").append(getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return this.function ? "#function" : "#macro";
    }

    public boolean isFunction() {
        return this.function;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Macro$Context.class */
    class Context implements LocalContext {
        final Environment.Namespace localVars;
        final TemplateObject callPlace;
        final Environment.Namespace nestedContentNamespace;
        final List<String> nestedContentParameterNames;
        final LocalContextStack prevLocalContextStack;
        final Context prevMacroContext;
        TemplateModel argsSpecialVariableValue;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !Macro.class.desiredAssertionStatus();
        }

        Context(Environment env, TemplateObject callPlace, List<String> nestedContentParameterNames) {
            env.getClass();
            this.localVars = env.new Namespace();
            this.callPlace = callPlace;
            this.nestedContentNamespace = env.getCurrentNamespace();
            this.nestedContentParameterNames = nestedContentParameterNames;
            this.prevLocalContextStack = env.getLocalContextStack();
            this.prevMacroContext = env.getCurrentMacroContext();
        }

        Macro getMacro() {
            return Macro.this;
        }

        void checkParamsSetAndApplyDefaults(Environment env) throws TemplateException {
            TemplateModel[] argsSpecVarDraft;
            Expression firstUnresolvedDefaultValueExpression;
            InvalidReferenceException firstInvalidReferenceExceptionForDefaultValue;
            boolean hasUnresolvedDefaultValue;
            boolean resolvedADefaultValue;
            TemplateHashModelEx2 catchAllHash;
            if (Macro.this.requireArgsSpecialVariable) {
                argsSpecVarDraft = new TemplateModel[Macro.this.paramNames.length];
            } else {
                argsSpecVarDraft = null;
            }
            do {
                firstUnresolvedDefaultValueExpression = null;
                firstInvalidReferenceExceptionForDefaultValue = null;
                hasUnresolvedDefaultValue = false;
                resolvedADefaultValue = false;
                for (int paramIndex = 0; paramIndex < Macro.this.paramNames.length; paramIndex++) {
                    String argName = Macro.this.paramNames[paramIndex];
                    TemplateModel argValue = this.localVars.get(argName);
                    if (argValue == null) {
                        Expression defaultValueExp = (Expression) Macro.this.paramNamesWithDefault.get(argName);
                        if (defaultValueExp != null) {
                            try {
                                TemplateModel defaultValue = defaultValueExp.eval(env);
                                if (defaultValue == null) {
                                    if (!hasUnresolvedDefaultValue) {
                                        firstUnresolvedDefaultValueExpression = defaultValueExp;
                                        hasUnresolvedDefaultValue = true;
                                    }
                                } else {
                                    this.localVars.put(argName, defaultValue);
                                    resolvedADefaultValue = true;
                                    if (argsSpecVarDraft != null) {
                                        argsSpecVarDraft[paramIndex] = defaultValue;
                                    }
                                }
                            } catch (InvalidReferenceException e) {
                                if (!hasUnresolvedDefaultValue) {
                                    hasUnresolvedDefaultValue = true;
                                    firstInvalidReferenceExceptionForDefaultValue = e;
                                }
                            }
                        } else if (!env.isClassicCompatible()) {
                            boolean argWasSpecified = this.localVars.containsKey(argName);
                            Object[] objArr = new Object[10];
                            objArr[0] = "When calling ";
                            objArr[1] = Macro.this.isFunction() ? "function" : "macro";
                            objArr[2] = " ";
                            objArr[3] = new _DelayedJQuote(Macro.this.name);
                            objArr[4] = ", required parameter ";
                            objArr[5] = new _DelayedJQuote(argName);
                            objArr[6] = " (parameter #";
                            objArr[7] = Integer.valueOf(paramIndex + 1);
                            objArr[8] = ") was ";
                            objArr[9] = argWasSpecified ? "specified, but had null/missing value." : "not specified.";
                            throw new _MiscTemplateException(env, new _ErrorDescriptionBuilder(objArr).tip(argWasSpecified ? new Object[]{"If the parameter value expression on the caller side is known to be legally null/missing, you may want to specify a default value for it with the \"!\" operator, like paramValue!defaultValue."} : new Object[]{"If the omission was deliberate, you may consider making the parameter optional in the macro by specifying a default value for it, like ", "<#macro macroName paramName=defaultExpr>", ")"}));
                        }
                    } else if (argsSpecVarDraft != null) {
                        argsSpecVarDraft[paramIndex] = argValue;
                    }
                }
                if (!hasUnresolvedDefaultValue) {
                    break;
                }
            } while (resolvedADefaultValue);
            if (hasUnresolvedDefaultValue) {
                if (firstInvalidReferenceExceptionForDefaultValue != null) {
                    throw firstInvalidReferenceExceptionForDefaultValue;
                }
                if (!env.isClassicCompatible()) {
                    throw InvalidReferenceException.getInstance(firstUnresolvedDefaultValueExpression, env);
                }
            }
            if (argsSpecVarDraft != null) {
                String catchAllParamName = getMacro().catchAllParamName;
                TemplateModel catchAllArgValue = catchAllParamName != null ? this.localVars.get(catchAllParamName) : null;
                if (getMacro().isFunction()) {
                    int lengthWithCatchAlls = argsSpecVarDraft.length;
                    if (catchAllArgValue != null) {
                        lengthWithCatchAlls += ((TemplateSequenceModel) catchAllArgValue).size();
                    }
                    SimpleSequence argsSpecVarValue = new SimpleSequence(lengthWithCatchAlls, _ObjectWrappers.SAFE_OBJECT_WRAPPER);
                    for (TemplateModel templateModel : argsSpecVarDraft) {
                        argsSpecVarValue.add(templateModel);
                    }
                    if (catchAllParamName != null) {
                        TemplateSequenceModel catchAllSeq = (TemplateSequenceModel) catchAllArgValue;
                        int catchAllSize = catchAllSeq.size();
                        for (int j = 0; j < catchAllSize; j++) {
                            argsSpecVarValue.add(catchAllSeq.get(j));
                        }
                    }
                    if (!$assertionsDisabled && argsSpecVarValue.size() != lengthWithCatchAlls) {
                        throw new AssertionError();
                    }
                    this.argsSpecialVariableValue = argsSpecVarValue;
                    return;
                }
                int lengthWithCatchAlls2 = argsSpecVarDraft.length;
                if (catchAllParamName != null) {
                    if (catchAllArgValue instanceof TemplateSequenceModel) {
                        if (((TemplateSequenceModel) catchAllArgValue).size() != 0) {
                            throw new _MiscTemplateException("The macro can only by called with named arguments, because it uses both .", "args", " and a non-empty catch-all parameter.");
                        }
                        catchAllHash = Constants.EMPTY_HASH_EX2;
                    } else {
                        catchAllHash = (TemplateHashModelEx2) catchAllArgValue;
                    }
                    lengthWithCatchAlls2 += catchAllHash.size();
                } else {
                    catchAllHash = null;
                }
                SimpleHash argsSpecVarValue2 = new SimpleHash(new LinkedHashMap((lengthWithCatchAlls2 * 4) / 3, 1.0f), _ObjectWrappers.SAFE_OBJECT_WRAPPER, 0);
                for (int paramIndex2 = 0; paramIndex2 < argsSpecVarDraft.length; paramIndex2++) {
                    argsSpecVarValue2.put(Macro.this.paramNames[paramIndex2], argsSpecVarDraft[paramIndex2]);
                }
                if (catchAllArgValue != null) {
                    TemplateHashModelEx2.KeyValuePairIterator iter = catchAllHash.keyValuePairIterator();
                    while (iter.hasNext()) {
                        TemplateHashModelEx2.KeyValuePair kvp = iter.next();
                        argsSpecVarValue2.put(((TemplateScalarModel) kvp.getKey()).getAsString(), kvp.getValue());
                    }
                }
                if (!$assertionsDisabled && argsSpecVarValue2.size() != lengthWithCatchAlls2) {
                    throw new AssertionError();
                }
                this.argsSpecialVariableValue = argsSpecVarValue2;
            }
        }

        @Override // freemarker.core.LocalContext
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            return this.localVars.get(name);
        }

        Environment.Namespace getLocals() {
            return this.localVars;
        }

        void setLocalVar(String name, TemplateModel var) {
            this.localVars.put(name, var);
        }

        @Override // freemarker.core.LocalContext
        public Collection getLocalVariableNames() throws TemplateModelException {
            HashSet result = new HashSet();
            TemplateModelIterator it = this.localVars.keys().iterator();
            while (it.hasNext()) {
                result.add(((TemplateScalarModel) it.next()).getAsString());
            }
            return result;
        }

        TemplateModel getArgsSpecialVariableValue() {
            return this.argsSpecialVariableValue;
        }

        void setArgsSpecialVariableValue(TemplateModel argsSpecialVariableValue) {
            this.argsSpecialVariableValue = argsSpecialVariableValue;
        }
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1 + (this.paramNames.length * 2) + 1 + 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.name;
        }
        int argDescsEnd = (this.paramNames.length * 2) + 1;
        if (idx < argDescsEnd) {
            String paramName = this.paramNames[(idx - 1) / 2];
            if (idx % 2 != 0) {
                return paramName;
            }
            return this.paramNamesWithDefault.get(paramName);
        }
        if (idx == argDescsEnd) {
            return this.catchAllParamName;
        }
        if (idx == argDescsEnd + 1) {
            return Integer.valueOf(this.function ? 1 : 0);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.ASSIGNMENT_TARGET;
        }
        int argDescsEnd = (this.paramNames.length * 2) + 1;
        if (idx < argDescsEnd) {
            if (idx % 2 != 0) {
                return ParameterRole.PARAMETER_NAME;
            }
            return ParameterRole.PARAMETER_DEFAULT;
        }
        if (idx == argDescsEnd) {
            return ParameterRole.CATCH_ALL_PARAMETER_NAME;
        }
        if (idx == argDescsEnd + 1) {
            return ParameterRole.AST_NODE_SUBTYPE;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return true;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Macro$WithArgs.class */
    static final class WithArgs {
        private final TemplateHashModelEx byName;
        private final TemplateSequenceModel byPosition;
        private final boolean orderLast;

        WithArgs(TemplateHashModelEx byName, boolean orderLast) {
            this.byName = byName;
            this.byPosition = null;
            this.orderLast = orderLast;
        }

        WithArgs(TemplateSequenceModel byPosition, boolean orderLast) {
            this.byName = null;
            this.byPosition = byPosition;
            this.orderLast = orderLast;
        }

        public TemplateHashModelEx getByName() {
            return this.byName;
        }

        public TemplateSequenceModel getByPosition() {
            return this.byPosition;
        }

        public boolean isOrderLast() {
            return this.orderLast;
        }
    }
}
