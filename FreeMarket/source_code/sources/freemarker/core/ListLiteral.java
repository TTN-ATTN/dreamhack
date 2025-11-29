package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.springframework.beans.PropertyAccessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ListLiteral.class */
final class ListLiteral extends Expression {
    final ArrayList<Expression> items;

    ListLiteral(ArrayList<Expression> items) {
        this.items = items;
        items.trimToSize();
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        SimpleSequence list = new SimpleSequence(this.items.size(), _ObjectWrappers.SAFE_OBJECT_WRAPPER);
        Iterator<Expression> it = this.items.iterator();
        while (it.hasNext()) {
            Expression exp = it.next();
            TemplateModel tm = exp.eval(env);
            if (env == null || !env.isClassicCompatible()) {
                exp.assertNonNull(tm, env);
            }
            list.add(tm);
        }
        return list;
    }

    List getValueList(Environment env) throws TemplateException {
        int size = this.items.size();
        switch (size) {
            case 0:
                return Collections.EMPTY_LIST;
            case 1:
                return Collections.singletonList(this.items.get(0).evalAndCoerceToPlainText(env));
            default:
                List result = new ArrayList(this.items.size());
                ListIterator iterator = this.items.listIterator();
                while (iterator.hasNext()) {
                    Expression exp = iterator.next();
                    result.add(exp.evalAndCoerceToPlainText(env));
                }
                return result;
        }
    }

    List getModelList(Environment env) throws TemplateException {
        int size = this.items.size();
        switch (size) {
            case 0:
                return Collections.EMPTY_LIST;
            case 1:
                return Collections.singletonList(this.items.get(0).eval(env));
            default:
                List result = new ArrayList(this.items.size());
                ListIterator iterator = this.items.listIterator();
                while (iterator.hasNext()) {
                    Expression exp = iterator.next();
                    result.add(exp.eval(env));
                }
                return result;
        }
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder(PropertyAccessor.PROPERTY_KEY_PREFIX);
        int size = this.items.size();
        for (int i = 0; i < size; i++) {
            Expression value = this.items.get(i);
            buf.append(value.getCanonicalForm());
            if (i != size - 1) {
                buf.append(", ");
            }
        }
        buf.append("]");
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return DefaultTruncateBuiltinAlgorithm.STANDARD_ASCII_TERMINATOR;
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        if (this.constantValue != null) {
            return true;
        }
        for (int i = 0; i < this.items.size(); i++) {
            Expression exp = this.items.get(i);
            if (!exp.isLiteral()) {
                return false;
            }
        }
        return true;
    }

    TemplateSequenceModel evaluateStringsToNamespaces(Environment env) throws TemplateException {
        TemplateSequenceModel val = (TemplateSequenceModel) eval(env);
        SimpleSequence result = new SimpleSequence(val.size(), _ObjectWrappers.SAFE_OBJECT_WRAPPER);
        for (int i = 0; i < this.items.size(); i++) {
            Object itemExpr = this.items.get(i);
            if (itemExpr instanceof StringLiteral) {
                String s = ((StringLiteral) itemExpr).getAsString();
                try {
                    Environment.Namespace ns = env.importLib(s, (String) null);
                    result.add(ns);
                } catch (IOException ioe) {
                    throw new _MiscTemplateException((StringLiteral) itemExpr, "Couldn't import library ", new _DelayedJQuote(s), ": ", new _DelayedGetMessage(ioe));
                }
            } else {
                result.add(val.get(i));
            }
        }
        return result;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        ArrayList clonedValues = (ArrayList) this.items.clone();
        ListIterator iter = clonedValues.listIterator();
        while (iter.hasNext()) {
            iter.set(((Expression) iter.next()).deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        return new ListLiteral(clonedValues);
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        if (this.items != null) {
            return this.items.size();
        }
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        checkIndex(idx);
        return this.items.get(idx);
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        checkIndex(idx);
        return ParameterRole.ITEM_VALUE;
    }

    private void checkIndex(int idx) {
        if (this.items == null || idx >= this.items.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
}
