package freemarker.core;

import freemarker.core.Expression;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.StringReader;
import java.util.List;
import org.springframework.context.expression.StandardBeanExpressionResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/StringLiteral.class */
final class StringLiteral extends Expression implements TemplateScalarModel {
    private final String value;
    private List<Object> dynamicValue;

    StringLiteral(String value) {
        this.value = value;
    }

    void parseValue(FMParser parentParser, OutputFormat outputFormat) throws ParseException {
        Template parentTemplate = getTemplate();
        ParserConfiguration pcfg = parentTemplate.getParserConfiguration();
        int intSyn = pcfg.getInterpolationSyntax();
        if (this.value.length() > 3) {
            if (((intSyn == 20 || intSyn == 21) && (this.value.indexOf("${") != -1 || (intSyn == 20 && this.value.indexOf(StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX) != -1))) || (intSyn == 22 && this.value.indexOf("[=") != -1)) {
                try {
                    SimpleCharStream simpleCharacterStream = new SimpleCharStream(new StringReader(this.value), this.beginLine, this.beginColumn + 1, this.value.length());
                    simpleCharacterStream.setTabSize(pcfg.getTabSize());
                    FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharacterStream);
                    FMParser parser = new FMParser(parentTemplate, false, tkMan, pcfg);
                    parser.setupStringLiteralMode(parentParser, outputFormat);
                    try {
                        this.dynamicValue = parser.StaticTextAndInterpolations();
                        parser.tearDownStringLiteralMode(parentParser);
                        this.constantValue = null;
                    } catch (Throwable th) {
                        parser.tearDownStringLiteralMode(parentParser);
                        throw th;
                    }
                } catch (ParseException e) {
                    e.setTemplateName(parentTemplate.getSourceName());
                    throw e;
                }
            }
        }
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.dynamicValue == null) {
            return new SimpleScalar(this.value);
        }
        StringBuilder plainTextResult = null;
        TemplateMarkupOutputModel<?> markupResult = null;
        for (Object part : this.dynamicValue) {
            Object calcedPart = part instanceof String ? part : ((Interpolation) part).calculateInterpolatedStringOrMarkup(env);
            if (markupResult != null) {
                TemplateMarkupOutputModel<?> partMO = calcedPart instanceof String ? markupResult.getOutputFormat().fromPlainTextByEscaping((String) calcedPart) : (TemplateMarkupOutputModel) calcedPart;
                markupResult = EvalUtil.concatMarkupOutputs(this, markupResult, partMO);
            } else if (calcedPart instanceof String) {
                String partStr = (String) calcedPart;
                if (plainTextResult == null) {
                    plainTextResult = new StringBuilder(partStr);
                } else {
                    plainTextResult.append(partStr);
                }
            } else {
                TemplateMarkupOutputModel<?> moPart = (TemplateMarkupOutputModel) calcedPart;
                if (plainTextResult != null) {
                    TemplateMarkupOutputModel<?> leftHandMO = moPart.getOutputFormat().fromPlainTextByEscaping(plainTextResult.toString());
                    markupResult = EvalUtil.concatMarkupOutputs(this, leftHandMO, moPart);
                    plainTextResult = null;
                } else {
                    markupResult = moPart;
                }
            }
        }
        return markupResult != null ? markupResult : plainTextResult != null ? new SimpleScalar(plainTextResult.toString()) : SimpleScalar.EMPTY_STRING;
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        return this.value;
    }

    boolean isSingleInterpolationLiteral() {
        return this.dynamicValue != null && this.dynamicValue.size() == 1 && (this.dynamicValue.get(0) instanceof Interpolation);
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        if (this.dynamicValue == null) {
            return StringUtil.ftlQuote(this.value);
        }
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        for (Object child : this.dynamicValue) {
            if (child instanceof Interpolation) {
                sb.append(((Interpolation) child).getCanonicalFormInStringLiteral());
            } else {
                sb.append(StringUtil.FTLStringLiteralEnc((String) child, '\"'));
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return this.dynamicValue == null ? getCanonicalForm() : "dynamic \"...\"";
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return this.dynamicValue == null;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        StringLiteral cloned = new StringLiteral(this.value);
        cloned.dynamicValue = this.dynamicValue;
        return cloned;
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        if (this.dynamicValue == null) {
            return 0;
        }
        return this.dynamicValue.size();
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        checkIndex(idx);
        return this.dynamicValue.get(idx);
    }

    private void checkIndex(int idx) {
        if (this.dynamicValue == null || idx >= this.dynamicValue.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        checkIndex(idx);
        return ParameterRole.VALUE_PART;
    }
}
