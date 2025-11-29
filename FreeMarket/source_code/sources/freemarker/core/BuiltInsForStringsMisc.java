package freemarker.core;

import freemarker.core.JSONParser;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.io.StringReader;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc.class */
class BuiltInsForStringsMisc {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$booleanBI.class */
    static class booleanBI extends BuiltInForString {
        booleanBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            boolean b;
            if (s.equals("true")) {
                b = true;
            } else if (s.equals("false")) {
                b = false;
            } else if (s.equals(env.getTrueStringValue())) {
                b = true;
            } else if (s.equals(env.getFalseStringValue())) {
                b = false;
            } else {
                throw new _MiscTemplateException(this, env, "Can't convert this string to boolean: ", new _DelayedJQuote(s));
            }
            return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$evalBI.class */
    static class evalBI extends OutputFormatBoundBuiltIn {
        evalBI() {
        }

        @Override // freemarker.core.OutputFormatBoundBuiltIn
        protected TemplateModel calculateResult(Environment env) throws TemplateException {
            return calculateResult(BuiltInForString.getTargetString(this.target, env), env);
        }

        TemplateModel calculateResult(String s, Environment env) throws ParseException, TemplateException {
            Template parentTemplate = getTemplate();
            try {
                try {
                    ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
                    SimpleCharStream simpleCharStream = new SimpleCharStream(new StringReader("(" + s + ")"), -1000000000, 1, s.length() + 2);
                    simpleCharStream.setTabSize(pCfg.getTabSize());
                    FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharStream);
                    tkMan.SwitchTo(2);
                    if (pCfg.getOutputFormat() != this.outputFormat) {
                        pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, Integer.valueOf(this.autoEscapingPolicy));
                    }
                    FMParser parser = new FMParser(parentTemplate, false, tkMan, pCfg);
                    Expression exp = parser.Expression();
                    try {
                        return exp.eval(env);
                    } catch (TemplateException e) {
                        throw new _MiscTemplateException(e, this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---", "\n\nThe failing expression:");
                    }
                } catch (TokenMgrError e2) {
                    throw e2.toParseException(parentTemplate);
                }
            } catch (ParseException e3) {
                throw new _MiscTemplateException(this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e3), "\n---end-message---", "\n\nThe failing expression:");
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$evalJsonBI.class */
    static class evalJsonBI extends BuiltInForString {
        evalJsonBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            try {
                return JSONParser.parse(s);
            } catch (JSONParser.JSONParseException e) {
                throw new _MiscTemplateException(this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failing expression:");
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$numberBI.class */
    static class numberBI extends BuiltInForString {
        numberBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            try {
                return new SimpleNumber(env.getArithmeticEngine().toNumber(s));
            } catch (NumberFormatException e) {
                throw NonNumericalException.newMalformedNumberException(this, s, env);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$absolute_template_nameBI.class */
    static class absolute_template_nameBI extends BuiltInForString {
        absolute_template_nameBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new AbsoluteTemplateNameResult(s, env);
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsMisc$absolute_template_nameBI$AbsoluteTemplateNameResult.class */
        private class AbsoluteTemplateNameResult implements TemplateScalarModel, TemplateMethodModelEx {
            private final String pathToResolve;
            private final Environment env;

            public AbsoluteTemplateNameResult(String pathToResolve, Environment env) {
                this.pathToResolve = pathToResolve;
                this.env = env;
            }

            @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
            public Object exec(List args) throws TemplateModelException {
                absolute_template_nameBI.this.checkMethodArgCount(args, 1);
                return resolvePath(absolute_template_nameBI.this.getStringMethodArg(args, 0));
            }

            @Override // freemarker.template.TemplateScalarModel
            public String getAsString() throws TemplateModelException {
                return resolvePath(absolute_template_nameBI.this.getTemplate().getName());
            }

            private String resolvePath(String basePath) throws TemplateModelException {
                try {
                    return this.env.rootBasedToAbsoluteTemplateName(this.env.toFullTemplateName(basePath, this.pathToResolve));
                } catch (MalformedTemplateNameException e) {
                    throw new _TemplateModelException(e, "Can't resolve ", new _DelayedJQuote(this.pathToResolve), "to absolute template name using base ", new _DelayedJQuote(basePath), "; see cause exception");
                }
            }
        }
    }

    private BuiltInsForStringsMisc() {
    }
}
