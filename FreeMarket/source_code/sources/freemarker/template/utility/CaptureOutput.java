package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/CaptureOutput.class */
public class CaptureOutput implements TemplateTransformModel {
    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(final Writer out, Map args) throws TemplateModelException {
        if (args == null) {
            throw new TemplateModelException("Must specify the name of the variable in which to capture the output with the 'var' or 'local' or 'global' parameter.");
        }
        boolean local = false;
        boolean global = false;
        final TemplateModel nsModel = (TemplateModel) args.get("namespace");
        Object varNameModel = args.get("var");
        if (varNameModel == null) {
            varNameModel = args.get("local");
            if (varNameModel == null) {
                varNameModel = args.get("global");
                global = true;
            } else {
                local = true;
            }
            if (varNameModel == null) {
                throw new TemplateModelException("Must specify the name of the variable in which to capture the output with the 'var' or 'local' or 'global' parameter.");
            }
        }
        if (args.size() == 2) {
            if (nsModel == null) {
                throw new TemplateModelException("Second parameter can only be namespace");
            }
            if (local) {
                throw new TemplateModelException("Cannot specify namespace for a local assignment");
            }
            if (global) {
                throw new TemplateModelException("Cannot specify namespace for a global assignment");
            }
            if (!(nsModel instanceof Environment.Namespace)) {
                throw new TemplateModelException("namespace parameter does not specify a namespace. It is a " + nsModel.getClass().getName());
            }
        } else if (args.size() != 1) {
            throw new TemplateModelException("Bad parameters. Use only one of 'var' or 'local' or 'global' parameters.");
        }
        if (!(varNameModel instanceof TemplateScalarModel)) {
            throw new TemplateModelException("'var' or 'local' or 'global' parameter doesn't evaluate to a string");
        }
        final String varName = ((TemplateScalarModel) varNameModel).getAsString();
        if (varName == null) {
            throw new TemplateModelException("'var' or 'local' or 'global' parameter evaluates to null string");
        }
        final StringBuilder buf = new StringBuilder();
        final Environment env = Environment.getCurrentEnvironment();
        final boolean localVar = local;
        final boolean globalVar = global;
        return new Writer() { // from class: freemarker.template.utility.CaptureOutput.1
            @Override // java.io.Writer
            public void write(char[] cbuf, int off, int len) {
                buf.append(cbuf, off, len);
            }

            @Override // java.io.Writer, java.io.Flushable
            public void flush() throws IOException {
                out.flush();
            }

            @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                SimpleScalar result = new SimpleScalar(buf.toString());
                try {
                    if (localVar) {
                        env.setLocalVariable(varName, result);
                    } else if (globalVar) {
                        env.setGlobalVariable(varName, result);
                    } else if (nsModel == null) {
                        env.setVariable(varName, result);
                    } else {
                        ((Environment.Namespace) nsModel).put(varName, result);
                    }
                } catch (IllegalStateException ise) {
                    throw new IOException("Could not set variable " + varName + ": " + ise.getMessage());
                }
            }
        };
    }
}
