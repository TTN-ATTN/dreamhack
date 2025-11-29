package freemarker.core;

import freemarker.template.EmptyMap;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TransformBlock.class */
final class TransformBlock extends TemplateElement {
    private Expression transformExpression;
    Map namedArgs;
    private volatile transient SoftReference sortedNamedArgsCache;

    TransformBlock(Expression transformExpression, Map namedArgs, TemplateElements children) {
        this.transformExpression = transformExpression;
        this.namedArgs = namedArgs;
        setChildren(children);
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Map args;
        TemplateTransformModel ttm = env.getTransform(this.transformExpression);
        if (ttm != null) {
            if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
                args = new HashMap();
                for (Map.Entry entry : this.namedArgs.entrySet()) {
                    String key = (String) entry.getKey();
                    Expression valueExp = (Expression) entry.getValue();
                    TemplateModel value = valueExp.eval(env);
                    args.put(key, value);
                }
            } else {
                args = EmptyMap.instance;
            }
            env.visitAndTransform(getChildBuffer(), ttm, args);
            return null;
        }
        TemplateModel tm = this.transformExpression.eval(env);
        throw new UnexpectedTypeException(this.transformExpression, tm, "transform", new Class[]{TemplateTransformModel.class}, env);
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol());
        sb.append(' ');
        sb.append(this.transformExpression);
        if (this.namedArgs != null) {
            for (Map.Entry entry : getSortedNamedArgs()) {
                sb.append(' ');
                sb.append(entry.getKey());
                sb.append('=');
                _MessageUtil.appendExpressionAsUntearable(sb, (Expression) entry.getValue());
            }
        }
        if (canonical) {
            sb.append(">");
            sb.append(getChildrenCanonicalForm());
            sb.append("</").append(getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#transform";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1 + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0);
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.transformExpression;
        }
        if (this.namedArgs != null && idx - 1 < this.namedArgs.size() * 2) {
            Map.Entry namedArg = (Map.Entry) getSortedNamedArgs().get((idx - 1) / 2);
            return (idx - 1) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.CALLEE;
        }
        if (idx - 1 < this.namedArgs.size() * 2) {
            return (idx - 1) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
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

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
