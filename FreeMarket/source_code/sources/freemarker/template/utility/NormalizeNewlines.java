package freemarker.template.utility;

import freemarker.template.TemplateTransformModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/NormalizeNewlines.class */
public class NormalizeNewlines implements TemplateTransformModel {
    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(final Writer out, Map args) {
        final StringBuilder buf = new StringBuilder();
        return new Writer() { // from class: freemarker.template.utility.NormalizeNewlines.1
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
                StringReader sr = new StringReader(buf.toString());
                StringWriter sw = new StringWriter();
                NormalizeNewlines.this.transform(sr, sw);
                out.write(sw.toString());
            }
        };
    }

    public void transform(Reader in, Writer out) throws IOException {
        BufferedReader br = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
        PrintWriter pw = out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out);
        String line = br.readLine();
        if (line != null && line.length() > 0) {
            pw.println(line);
        }
        while (true) {
            String line2 = br.readLine();
            if (line2 != null) {
                pw.println(line2);
            } else {
                return;
            }
        }
    }
}
