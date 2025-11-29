package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.TemplateException;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/EscapeBlock.class */
class EscapeBlock extends TemplateElement {
    private final String variable;
    private final Expression expr;
    private Expression escapedExpr;

    EscapeBlock(String variable, Expression expr, Expression escapedExpr) {
        this.variable = variable;
        this.expr = expr;
        this.escapedExpr = escapedExpr;
    }

    void setContent(TemplateElements children) {
        setChildren(children);
        this.escapedExpr = null;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return getChildBuffer();
    }

    Expression doEscape(Expression expression) throws ParseException {
        try {
            return this.escapedExpr.deepCloneWithIdentifierReplaced(this.variable, expression, new Expression.ReplacemenetState());
        } catch (UncheckedParseException e) {
            throw e.getParseException();
        }
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(getNodeTypeSymbol()).append(' ').append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.variable)).append(" as ").append(this.expr.getCanonicalForm());
        if (canonical) {
            sb.append('>');
            sb.append(getChildrenCanonicalForm());
            sb.append("</").append(getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#escape";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 2;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.variable;
            case 1:
                return this.expr;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.PLACEHOLDER_VARIABLE;
            case 1:
                return ParameterRole.EXPRESSION_TEMPLATE;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isOutputCacheable() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
