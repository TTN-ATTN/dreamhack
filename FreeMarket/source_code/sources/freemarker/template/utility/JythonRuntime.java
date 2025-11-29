package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/JythonRuntime.class */
public class JythonRuntime extends PythonInterpreter implements TemplateTransformModel {
    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(final Writer out, Map args) {
        final StringBuilder buf = new StringBuilder();
        final Environment env = Environment.getCurrentEnvironment();
        return new Writer() { // from class: freemarker.template.utility.JythonRuntime.1
            @Override // java.io.Writer
            public void write(char[] cbuf, int off, int len) {
                buf.append(cbuf, off, len);
            }

            @Override // java.io.Writer, java.io.Flushable
            public void flush() throws IOException {
                interpretBuffer();
                out.flush();
            }

            @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
                interpretBuffer();
            }

            private void interpretBuffer() {
                synchronized (JythonRuntime.this) {
                    PyObject prevOut = JythonRuntime.this.systemState.stdout;
                    try {
                        JythonRuntime.this.setOut(out);
                        JythonRuntime.this.set("env", env);
                        JythonRuntime.this.exec(buf.toString());
                        buf.setLength(0);
                        JythonRuntime.this.setOut(prevOut);
                    } catch (Throwable th) {
                        JythonRuntime.this.setOut(prevOut);
                        throw th;
                    }
                }
            }
        };
    }
}
