package freemarker.core;

import freemarker.template.EmptyMap;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ObjectFactory;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnifiedCall.class */
final class UnifiedCall extends TemplateElement implements DirectiveCallPlace {
    private Expression nameExp;
    private Map<String, ? extends Expression> namedArgs;
    private List<? extends Expression> positionalArgs;
    private List<String> bodyParameterNames;
    boolean legacySyntax;
    private volatile transient SoftReference sortedNamedArgsCache;
    private CustomDataHolder customDataHolder;

    UnifiedCall(Expression nameExp, Map<String, ? extends Expression> namedArgs, TemplateElements children, List<String> bodyParameterNames) {
        this.nameExp = nameExp;
        this.namedArgs = namedArgs;
        setChildren(children);
        this.bodyParameterNames = bodyParameterNames;
    }

    UnifiedCall(Expression nameExp, List<? extends Expression> positionalArgs, TemplateElements children, List<String> bodyParameterNames) {
        this.nameExp = nameExp;
        this.positionalArgs = positionalArgs;
        setChildren(children);
        this.bodyParameterNames = bodyParameterNames;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Map args;
        TemplateModel tm = this.nameExp.eval(env);
        if (tm == Macro.DO_NOTHING_MACRO) {
            return null;
        }
        if (tm instanceof Macro) {
            Macro macro = (Macro) tm;
            if (macro.isFunction() && !this.legacySyntax) {
                throw new _MiscTemplateException(env, "Routine ", new _DelayedJQuote(macro.getName()), " is a function, not a directive. Functions can only be called from expressions, like in ${f()}, ${x + f()} or ", "<@someDirective someParam=f() />", ".");
            }
            env.invokeMacro(macro, this.namedArgs, this.positionalArgs, this.bodyParameterNames, this);
            return null;
        }
        boolean isDirectiveModel = tm instanceof TemplateDirectiveModel;
        if (!isDirectiveModel && !(tm instanceof TemplateTransformModel)) {
            if (tm == null) {
                throw InvalidReferenceException.getInstance(this.nameExp, env);
            }
            throw new NonUserDefinedDirectiveLikeException(this.nameExp, tm, env);
        }
        if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
            args = new HashMap();
            for (Map.Entry<String, ? extends Expression> entry : this.namedArgs.entrySet()) {
                String key = entry.getKey();
                Expression valueExp = entry.getValue();
                TemplateModel value = valueExp.eval(env);
                args.put(key, value);
            }
        } else {
            args = EmptyMap.instance;
        }
        if (isDirectiveModel) {
            env.visit(getChildBuffer(), (TemplateDirectiveModel) tm, args, this.bodyParameterNames);
            return null;
        }
        env.visitAndTransform(getChildBuffer(), (TemplateTransformModel) tm, args);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append('@');
        _MessageUtil.appendExpressionAsUntearable(sb, this.nameExp);
        boolean nameIsInParen = sb.charAt(sb.length() - 1) == ')';
        if (this.positionalArgs != null) {
            for (int i = 0; i < this.positionalArgs.size(); i++) {
                Expression argExp = this.positionalArgs.get(i);
                if (i != 0) {
                    sb.append(',');
                }
                sb.append(' ');
                sb.append(argExp.getCanonicalForm());
            }
        } else {
            List entries = getSortedNamedArgs();
            for (int i2 = 0; i2 < entries.size(); i2++) {
                Map.Entry entry = (Map.Entry) entries.get(i2);
                Expression argExp2 = (Expression) entry.getValue();
                sb.append(' ');
                sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference((String) entry.getKey()));
                sb.append('=');
                _MessageUtil.appendExpressionAsUntearable(sb, argExp2);
            }
        }
        if (this.bodyParameterNames != null && !this.bodyParameterNames.isEmpty()) {
            sb.append("; ");
            for (int i3 = 0; i3 < this.bodyParameterNames.size(); i3++) {
                if (i3 != 0) {
                    sb.append(", ");
                }
                sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.bodyParameterNames.get(i3)));
            }
        }
        if (canonical) {
            if (getChildCount() == 0) {
                sb.append("/>");
            } else {
                sb.append('>');
                sb.append(getChildrenCanonicalForm());
                sb.append("</@");
                if (!nameIsInParen && ((this.nameExp instanceof Identifier) || ((this.nameExp instanceof Dot) && ((Dot) this.nameExp).onlyHasIdentifiers()))) {
                    sb.append(this.nameExp.getCanonicalForm());
                }
                sb.append('>');
            }
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "@";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1 + (this.positionalArgs != null ? this.positionalArgs.size() : 0) + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0) + (this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0);
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.nameExp;
        }
        int positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
        if (idx - 1 < positionalArgsSize) {
            return this.positionalArgs.get(idx - 1);
        }
        int base = 1 + positionalArgsSize;
        int namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
        if (idx - base < namedArgsSize * 2) {
            Map.Entry namedArg = (Map.Entry) getSortedNamedArgs().get((idx - base) / 2);
            return (idx - base) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
        }
        int base2 = base + (namedArgsSize * 2);
        int bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
        if (idx - base2 < bodyParameterNamesSize) {
            return this.bodyParameterNames.get(idx - base2);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.CALLEE;
        }
        int positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
        if (idx - 1 < positionalArgsSize) {
            return ParameterRole.ARGUMENT_VALUE;
        }
        int base = 1 + positionalArgsSize;
        int namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
        if (idx - base < namedArgsSize * 2) {
            return (idx - base) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
        }
        int base2 = base + (namedArgsSize * 2);
        int bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
        if (idx - base2 < bodyParameterNamesSize) {
            return ParameterRole.TARGET_LOOP_VARIABLE;
        }
        throw new IndexOutOfBoundsException();
    }

    private List getSortedNamedArgs() {
        List res;
        Reference ref = this.sortedNamedArgsCache;
        if (ref != null && (res = (List) ref.get()) != null) {
            return res;
        }
        List res2 = MiscUtil.sortMapOfExpressions(this.namedArgs);
        this.sortedNamedArgsCache = new SoftReference(res2);
        return res2;
    }

    @Override // freemarker.core.DirectiveCallPlace
    @SuppressFBWarnings(value = {"IS2_INCONSISTENT_SYNC", "DC_DOUBLECHECK"}, justification = "Performance tricks")
    public Object getOrCreateCustomData(Object providerIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
        CustomDataHolder customDataHolder = this.customDataHolder;
        if (customDataHolder == null) {
            synchronized (this) {
                customDataHolder = this.customDataHolder;
                if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
                    customDataHolder = createNewCustomData(providerIdentity, objectFactory);
                    this.customDataHolder = customDataHolder;
                }
            }
        }
        if (customDataHolder.providerIdentity != providerIdentity) {
            synchronized (this) {
                customDataHolder = this.customDataHolder;
                if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
                    customDataHolder = createNewCustomData(providerIdentity, objectFactory);
                    this.customDataHolder = customDataHolder;
                }
            }
        }
        return customDataHolder.customData;
    }

    private CustomDataHolder createNewCustomData(Object provierIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
        try {
            Object customData = objectFactory.createObject();
            if (customData == null) {
                throw new NullPointerException("ObjectFactory.createObject() has returned null");
            }
            CustomDataHolder customDataHolder = new CustomDataHolder(provierIdentity, customData);
            return customDataHolder;
        } catch (Exception e) {
            throw new CallPlaceCustomDataInitializationException("Failed to initialize custom data for provider identity " + StringUtil.tryToString(provierIdentity) + " via factory " + StringUtil.tryToString(objectFactory), e);
        }
    }

    @Override // freemarker.core.DirectiveCallPlace
    public boolean isNestedOutputCacheable() {
        return isChildrenOutputCacheable();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnifiedCall$CustomDataHolder.class */
    private static class CustomDataHolder {
        private final Object providerIdentity;
        private final Object customData;

        public CustomDataHolder(Object providerIdentity, Object customData) {
            this.providerIdentity = providerIdentity;
            this.customData = customData;
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
