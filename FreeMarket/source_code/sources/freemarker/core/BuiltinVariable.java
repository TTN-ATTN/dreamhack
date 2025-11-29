package freemarker.core;

import freemarker.core.Expression;
import freemarker.core.Macro;
import freemarker.template.Configuration;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._VersionInts;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltinVariable.class */
final class BuiltinVariable extends Expression {
    static final String LANG = "lang";
    static final String LOCALE = "locale";
    static final String TIME_ZONE_CC = "timeZone";
    static final String TIME_ZONE = "time_zone";
    static final String INCOMPATIBLE_IMPROVEMENTS_CC = "incompatibleImprovements";
    static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
    static final String OUTPUT_ENCODING_CC = "outputEncoding";
    static final String OUTPUT_ENCODING = "output_encoding";
    static final String OUTPUT_FORMAT_CC = "outputFormat";
    static final String OUTPUT_FORMAT = "output_format";
    static final String URL_ESCAPING_CHARSET_CC = "urlEscapingCharset";
    static final String URL_ESCAPING_CHARSET = "url_escaping_charset";
    private final String name;
    private final TemplateModel parseTimeValue;
    static final String ARGS = "args";
    static final String AUTO_ESC_CC = "autoEsc";
    static final String AUTO_ESC = "auto_esc";
    static final String CALLER_TEMPLATE_NAME_CC = "callerTemplateName";
    static final String CALLER_TEMPLATE_NAME = "caller_template_name";
    static final String CURRENT_NODE_CC = "currentNode";
    static final String CURRENT_TEMPLATE_NAME_CC = "currentTemplateName";
    static final String CURRENT_NODE = "current_node";
    static final String CURRENT_TEMPLATE_NAME = "current_template_name";
    static final String DATA_MODEL_CC = "dataModel";
    static final String DATA_MODEL = "data_model";
    static final String ERROR = "error";
    static final String GET_OPTIONAL_TEMPLATE_CC = "getOptionalTemplate";
    static final String GET_OPTIONAL_TEMPLATE = "get_optional_template";
    static final String GLOBALS = "globals";
    static final String LOCALE_OBJECT_CC = "localeObject";
    static final String LOCALE_OBJECT = "locale_object";
    static final String LOCALS = "locals";
    static final String MAIN = "main";
    static final String MAIN_TEMPLATE_NAME_CC = "mainTemplateName";
    static final String MAIN_TEMPLATE_NAME = "main_template_name";
    static final String NAMESPACE = "namespace";
    static final String NODE = "node";
    static final String NOW = "now";
    static final String PASS = "pass";
    static final String TEMPLATE_NAME_CC = "templateName";
    static final String TEMPLATE_NAME = "template_name";
    static final String VARS = "vars";
    static final String VERSION = "version";
    static final String[] SPEC_VAR_NAMES = {ARGS, AUTO_ESC_CC, AUTO_ESC, CALLER_TEMPLATE_NAME_CC, CALLER_TEMPLATE_NAME, CURRENT_NODE_CC, CURRENT_TEMPLATE_NAME_CC, CURRENT_NODE, CURRENT_TEMPLATE_NAME, DATA_MODEL_CC, DATA_MODEL, ERROR, GET_OPTIONAL_TEMPLATE_CC, GET_OPTIONAL_TEMPLATE, GLOBALS, "incompatibleImprovements", "incompatible_improvements", "lang", "locale", LOCALE_OBJECT_CC, LOCALE_OBJECT, LOCALS, MAIN, MAIN_TEMPLATE_NAME_CC, MAIN_TEMPLATE_NAME, NAMESPACE, NODE, NOW, "outputEncoding", "outputFormat", "output_encoding", "output_format", PASS, TEMPLATE_NAME_CC, TEMPLATE_NAME, "timeZone", "time_zone", "urlEscapingCharset", "url_escaping_charset", VARS, VERSION};

    /* JADX WARN: Removed duplicated region for block: B:34:0x00ef  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    BuiltinVariable(freemarker.core.Token r7, freemarker.core.FMParserTokenManager r8, freemarker.template.TemplateModel r9) throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 297
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.BuiltinVariable.<init>(freemarker.core.Token, freemarker.core.FMParserTokenManager, freemarker.template.TemplateModel):void");
    }

    @Override // freemarker.core.Expression
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.parseTimeValue != null) {
            return this.parseTimeValue;
        }
        if (this.name == NAMESPACE) {
            return env.getCurrentNamespace();
        }
        if (this.name == MAIN) {
            return env.getMainNamespace();
        }
        if (this.name == GLOBALS) {
            return env.getGlobalVariables();
        }
        if (this.name == LOCALS) {
            Macro.Context ctx = env.getCurrentMacroContext();
            if (ctx == null) {
                return null;
            }
            return ctx.getLocals();
        }
        if (this.name == DATA_MODEL || this.name == DATA_MODEL_CC) {
            return env.getDataModel();
        }
        if (this.name == VARS) {
            return new VarsHash(env);
        }
        if (this.name == "locale") {
            return new SimpleScalar(env.getLocale().toString());
        }
        if (this.name == LOCALE_OBJECT || this.name == LOCALE_OBJECT_CC) {
            return env.getObjectWrapper().wrap(env.getLocale());
        }
        if (this.name == "lang") {
            return new SimpleScalar(env.getLocale().getLanguage());
        }
        if (this.name == CURRENT_NODE || this.name == NODE || this.name == CURRENT_NODE_CC) {
            return env.getCurrentVisitorNode();
        }
        if (this.name == TEMPLATE_NAME || this.name == TEMPLATE_NAME_CC) {
            if (env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_23) {
                return new SimpleScalar(env.getTemplate230().getName());
            }
            return new SimpleScalar(env.getTemplate().getName());
        }
        if (this.name == MAIN_TEMPLATE_NAME || this.name == MAIN_TEMPLATE_NAME_CC) {
            return SimpleScalar.newInstanceOrNull(env.getMainTemplate().getName());
        }
        if (this.name == CURRENT_TEMPLATE_NAME || this.name == CURRENT_TEMPLATE_NAME_CC) {
            return SimpleScalar.newInstanceOrNull(env.getCurrentTemplate().getName());
        }
        if (this.name == PASS) {
            return Macro.DO_NOTHING_MACRO;
        }
        if (this.name == "output_encoding" || this.name == "outputEncoding") {
            String s = env.getOutputEncoding();
            return SimpleScalar.newInstanceOrNull(s);
        }
        if (this.name == "url_escaping_charset" || this.name == "urlEscapingCharset") {
            String s2 = env.getURLEscapingCharset();
            return SimpleScalar.newInstanceOrNull(s2);
        }
        if (this.name == ERROR) {
            return new SimpleScalar(env.getCurrentRecoveredErrorMessage());
        }
        if (this.name == NOW) {
            return new SimpleDate(new Date(), 3);
        }
        if (this.name == VERSION) {
            return new SimpleScalar(Configuration.getVersionNumber());
        }
        if (this.name == "incompatible_improvements" || this.name == "incompatibleImprovements") {
            return new SimpleScalar(env.getConfiguration().getIncompatibleImprovements().toString());
        }
        if (this.name == GET_OPTIONAL_TEMPLATE) {
            return GetOptionalTemplateMethod.INSTANCE;
        }
        if (this.name == GET_OPTIONAL_TEMPLATE_CC) {
            return GetOptionalTemplateMethod.INSTANCE_CC;
        }
        if (this.name == CALLER_TEMPLATE_NAME || this.name == CALLER_TEMPLATE_NAME_CC) {
            TemplateObject callPlace = getRequiredMacroContext(env).callPlace;
            String name = callPlace != null ? callPlace.getTemplate().getName() : null;
            return name != null ? new SimpleScalar(name) : TemplateScalarModel.EMPTY_STRING;
        }
        if (this.name == ARGS) {
            TemplateModel args = getRequiredMacroContext(env).getArgsSpecialVariableValue();
            if (args == null) {
                throw new _MiscTemplateException(this, "The \"", ARGS, "\" special variable wasn't initialized.", this.name);
            }
            return args;
        }
        if (this.name == "time_zone" || this.name == "timeZone") {
            return new SimpleScalar(env.getTimeZone().getID());
        }
        throw new _MiscTemplateException(this, "Invalid special variable: ", this.name);
    }

    private Macro.Context getRequiredMacroContext(Environment env) throws TemplateException {
        Macro.Context ctx = env.getCurrentMacroContext();
        if (ctx == null) {
            throw new TemplateException("Can't get ." + this.name + " here, as there's no macro or function (that's implemented in the template) call in context.", env);
        }
        return ctx;
    }

    @Override // freemarker.core.TemplateObject
    public String toString() {
        return "." + this.name;
    }

    @Override // freemarker.core.TemplateObject
    public String getCanonicalForm() {
        return "." + this.name;
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return getCanonicalForm();
    }

    @Override // freemarker.core.Expression
    boolean isLiteral() {
        return false;
    }

    @Override // freemarker.core.Expression
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return this;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltinVariable$VarsHash.class */
    static class VarsHash implements TemplateHashModel {
        Environment env;

        VarsHash(Environment env) {
            this.env = env;
        }

        @Override // freemarker.template.TemplateHashModel
        public TemplateModel get(String key) throws TemplateModelException {
            return this.env.getVariable(key);
        }

        @Override // freemarker.template.TemplateHashModel
        public boolean isEmpty() {
            return false;
        }
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 0;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
}
