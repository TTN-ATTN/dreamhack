package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DollarVariable.class */
final class DollarVariable extends Interpolation {
    private final Expression expression;
    private final Expression escapedExpression;
    private final OutputFormat outputFormat;
    private final MarkupOutputFormat markupOutputFormat;
    private final boolean autoEscape;

    DollarVariable(Expression expression, Expression escapedExpression, OutputFormat outputFormat, boolean autoEscape) {
        this.expression = expression;
        this.escapedExpression = escapedExpression;
        this.outputFormat = outputFormat;
        this.markupOutputFormat = (MarkupOutputFormat) (outputFormat instanceof MarkupOutputFormat ? outputFormat : null);
        this.autoEscape = autoEscape;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Object moOrStr = calculateInterpolatedStringOrMarkup(env);
        Writer out = env.getOut();
        if (moOrStr instanceof String) {
            String s = (String) moOrStr;
            if (this.autoEscape) {
                this.markupOutputFormat.output(s, out);
                return null;
            }
            out.write(s);
            return null;
        }
        TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel) moOrStr;
        MarkupOutputFormat moOF = mo.getOutputFormat();
        if (moOF == this.outputFormat) {
            moOF.output((MarkupOutputFormat) mo, out);
            return null;
        }
        if (this.outputFormat.isOutputFormatMixingAllowed()) {
            if (this.markupOutputFormat != null) {
                this.markupOutputFormat.outputForeign(mo, out);
                return null;
            }
            moOF.output((MarkupOutputFormat) mo, out);
            return null;
        }
        String srcPlainText = moOF.getSourcePlainText(mo);
        if (srcPlainText == null) {
            throw new _TemplateModelException(this.escapedExpression, "The value to print is in ", new _DelayedToString(moOF), " format, which differs from the current output format, ", new _DelayedToString(this.outputFormat), ". Format conversion wasn't possible.");
        }
        if (this.markupOutputFormat != null) {
            this.markupOutputFormat.output(srcPlainText, out);
            return null;
        }
        out.write(srcPlainText);
        return null;
    }

    @Override // freemarker.core.Interpolation
    protected Object calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(this.escapedExpression.eval(env), this.escapedExpression, null, env);
    }

    @Override // freemarker.core.Interpolation
    protected String dump(boolean canonical, boolean inStringLiteral) {
        StringBuilder sb = new StringBuilder();
        int syntax = getTemplate().getInterpolationSyntax();
        sb.append(syntax != 22 ? "${" : "[=");
        String exprCF = this.expression.getCanonicalForm();
        sb.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '\"') : exprCF);
        sb.append(syntax != 22 ? "}" : "]");
        if (!canonical && this.expression != this.escapedExpression) {
            sb.append(" auto-escaped");
        }
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "${...}";
    }

    @Override // freemarker.core.TemplateElement
    boolean heedsOpeningWhitespace() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean heedsTrailingWhitespace() {
        return true;
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.expression;
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
