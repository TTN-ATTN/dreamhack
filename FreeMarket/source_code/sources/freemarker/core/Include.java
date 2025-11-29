package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Include.class */
final class Include extends TemplateElement {
    private final Expression includedTemplateNameExp;
    private final Expression encodingExp;
    private final Expression parseExp;
    private final Expression ignoreMissingExp;
    private final String encoding;
    private final Boolean parse;
    private final Boolean ignoreMissingExpPrecalcedValue;

    Include(Template template, Expression includedTemplatePathExp, Expression encodingExp, Expression parseExp, Expression ignoreMissingExp) throws ParseException {
        this.includedTemplateNameExp = includedTemplatePathExp;
        this.encodingExp = encodingExp;
        if (encodingExp != null && encodingExp.isLiteral()) {
            try {
                TemplateModel tm = encodingExp.eval(null);
                if (!(tm instanceof TemplateScalarModel)) {
                    throw new ParseException("Expected a string as the value of the \"encoding\" argument", encodingExp);
                }
                this.encoding = ((TemplateScalarModel) tm).getAsString();
            } catch (TemplateException e) {
                throw new BugException(e);
            }
        } else {
            this.encoding = null;
        }
        this.parseExp = parseExp;
        if (parseExp == null) {
            this.parse = Boolean.TRUE;
        } else if (parseExp.isLiteral()) {
            try {
                if (parseExp instanceof StringLiteral) {
                    this.parse = Boolean.valueOf(StringUtil.getYesNo(parseExp.evalAndCoerceToPlainText(null)));
                } else {
                    try {
                        this.parse = Boolean.valueOf(parseExp.evalToBoolean(template.getConfiguration()));
                    } catch (NonBooleanException e2) {
                        throw new ParseException("Expected a boolean or string as the value of the parse attribute", parseExp, e2);
                    }
                }
            } catch (TemplateException e3) {
                throw new BugException(e3);
            }
        } else {
            this.parse = null;
        }
        this.ignoreMissingExp = ignoreMissingExp;
        if (ignoreMissingExp != null) {
            try {
                if (ignoreMissingExp.isLiteral()) {
                    try {
                        this.ignoreMissingExpPrecalcedValue = Boolean.valueOf(ignoreMissingExp.evalToBoolean(template.getConfiguration()));
                        return;
                    } catch (NonBooleanException e4) {
                        throw new ParseException("Expected a boolean as the value of the \"ignore_missing\" attribute", ignoreMissingExp, e4);
                    }
                }
            } catch (TemplateException e5) {
                throw new BugException(e5);
            }
        }
        this.ignoreMissingExpPrecalcedValue = null;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        String strEvalAndCoerceToPlainText;
        boolean parse;
        boolean ignoreMissing;
        String includedTemplateName = this.includedTemplateNameExp.evalAndCoerceToPlainText(env);
        try {
            String fullIncludedTemplateName = env.toFullTemplateName(getTemplate().getName(), includedTemplateName);
            if (this.encoding != null) {
                strEvalAndCoerceToPlainText = this.encoding;
            } else {
                strEvalAndCoerceToPlainText = this.encodingExp != null ? this.encodingExp.evalAndCoerceToPlainText(env) : null;
            }
            String encoding = strEvalAndCoerceToPlainText;
            if (this.parse != null) {
                parse = this.parse.booleanValue();
            } else {
                TemplateModel tm = this.parseExp.eval(env);
                if (tm instanceof TemplateScalarModel) {
                    parse = getYesNo(this.parseExp, EvalUtil.modelToString((TemplateScalarModel) tm, this.parseExp, env));
                } else {
                    parse = this.parseExp.modelToBoolean(tm, env);
                }
            }
            if (this.ignoreMissingExpPrecalcedValue != null) {
                ignoreMissing = this.ignoreMissingExpPrecalcedValue.booleanValue();
            } else if (this.ignoreMissingExp != null) {
                ignoreMissing = this.ignoreMissingExp.evalToBoolean(env);
            } else {
                ignoreMissing = false;
            }
            try {
                Template includedTemplate = env.getTemplateForInclusion(fullIncludedTemplateName, encoding, parse, ignoreMissing);
                if (includedTemplate != null) {
                    env.include(includedTemplate);
                    return null;
                }
                return null;
            } catch (IOException e) {
                throw new _MiscTemplateException(e, env, "Template inclusion failed (for parameter value ", new _DelayedJQuote(includedTemplateName), "):\n", new _DelayedGetMessage(e));
            }
        } catch (MalformedTemplateNameException e2) {
            throw new _MiscTemplateException(e2, env, "Malformed template name ", new _DelayedJQuote(e2.getTemplateName()), ":\n", e2.getMalformednessDescription());
        }
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.includedTemplateNameExp.getCanonicalForm());
        if (this.encodingExp != null) {
            buf.append(" encoding=").append(this.encodingExp.getCanonicalForm());
        }
        if (this.parseExp != null) {
            buf.append(" parse=").append(this.parseExp.getCanonicalForm());
        }
        if (this.ignoreMissingExp != null) {
            buf.append(" ignore_missing=").append(this.ignoreMissingExp.getCanonicalForm());
        }
        if (canonical) {
            buf.append("/>");
        }
        return buf.toString();
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#include";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 4;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0:
                return this.includedTemplateNameExp;
            case 1:
                return this.parseExp;
            case 2:
                return this.encodingExp;
            case 3:
                return this.ignoreMissingExp;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0:
                return ParameterRole.TEMPLATE_NAME;
            case 1:
                return ParameterRole.PARSE_PARAMETER;
            case 2:
                return ParameterRole.ENCODING_PARAMETER;
            case 3:
                return ParameterRole.IGNORE_MISSING_PARAMETER;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }

    private boolean getYesNo(Expression exp, String s) throws TemplateException {
        try {
            return StringUtil.getYesNo(s);
        } catch (IllegalArgumentException e) {
            throw new _MiscTemplateException(exp, "Value must be boolean (or one of these strings: \"n\", \"no\", \"f\", \"false\", \"y\", \"yes\", \"t\", \"true\"), but it was ", new _DelayedJQuote(s), ".");
        }
    }

    @Override // freemarker.core.TemplateElement
    boolean isShownInStackTrace() {
        return true;
    }
}
