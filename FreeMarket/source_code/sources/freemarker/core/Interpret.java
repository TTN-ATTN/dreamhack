package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._VersionInts;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Interpret.class */
class Interpret extends OutputFormatBoundBuiltIn {
    Interpret() {
    }

    @Override // freemarker.core.OutputFormatBoundBuiltIn
    protected TemplateModel calculateResult(Environment env) throws TemplateException {
        Expression sourceExpr;
        TemplateModel model = this.target.eval(env);
        String id = "anonymous_interpreted";
        if (model instanceof TemplateSequenceModel) {
            sourceExpr = (Expression) new DynamicKeyName(this.target, new NumberLiteral(0)).copyLocationFrom(this.target);
            if (((TemplateSequenceModel) model).size() > 1) {
                id = ((Expression) new DynamicKeyName(this.target, new NumberLiteral(1)).copyLocationFrom(this.target)).evalAndCoerceToPlainText(env);
            }
        } else if (model instanceof TemplateScalarModel) {
            sourceExpr = this.target;
        } else {
            throw new UnexpectedTypeException(this.target, model, "sequence or string", new Class[]{TemplateSequenceModel.class, TemplateScalarModel.class}, env);
        }
        String templateSource = sourceExpr.evalAndCoerceToPlainText(env);
        Template parentTemplate = env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_26 ? env.getCurrentTemplate() : env.getTemplate();
        try {
            ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
            if (pCfg.getOutputFormat() != this.outputFormat) {
                pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, Integer.valueOf(this.autoEscapingPolicy));
            }
            Template interpretedTemplate = new Template((parentTemplate.getName() != null ? parentTemplate.getName() : "nameless_template") + "->" + id, null, new StringReader(templateSource), parentTemplate.getConfiguration(), pCfg, null);
            interpretedTemplate.setLocale(env.getLocale());
            return new TemplateProcessorModel(interpretedTemplate);
        } catch (IOException e) {
            throw new _MiscTemplateException(this, e, env, "Template parsing with \"?", this.key, "\" has failed with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failed expression:");
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/Interpret$TemplateProcessorModel.class */
    private class TemplateProcessorModel implements TemplateTransformModel {
        private final Template template;

        TemplateProcessorModel(Template template) {
            this.template = template;
        }

        @Override // freemarker.template.TemplateTransformModel
        public Writer getWriter(final Writer out, Map args) throws TemplateModelException, IOException {
            try {
                Environment env = Environment.getCurrentEnvironment();
                boolean lastFIRE = env.setFastInvalidReferenceExceptions(false);
                try {
                    env.include(this.template);
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                    return new Writer(out) { // from class: freemarker.core.Interpret.TemplateProcessorModel.1
                        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
                        public void close() {
                        }

                        @Override // java.io.Writer, java.io.Flushable
                        public void flush() throws IOException {
                            out.flush();
                        }

                        @Override // java.io.Writer
                        public void write(char[] cbuf, int off, int len) throws IOException {
                            out.write(cbuf, off, len);
                        }
                    };
                } catch (Throwable th) {
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                    throw th;
                }
            } catch (Exception e) {
                throw new _TemplateModelException(e, "Template created with \"?", Interpret.this.key, "\" has stopped with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---");
            }
        }
    }
}
