package freemarker.template.utility;

import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/XmlEscape.class */
public class XmlEscape implements TemplateTransformModel {
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();

    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(final Writer out, Map args) {
        return new Writer() { // from class: freemarker.template.utility.XmlEscape.1
            @Override // java.io.Writer
            public void write(int c) throws IOException {
                switch (c) {
                    case 34:
                        out.write(XmlEscape.QUOT, 0, 6);
                        break;
                    case 38:
                        out.write(XmlEscape.AMP, 0, 5);
                        break;
                    case 39:
                        out.write(XmlEscape.APOS, 0, 6);
                        break;
                    case 60:
                        out.write(XmlEscape.LT, 0, 4);
                        break;
                    case 62:
                        out.write(XmlEscape.GT, 0, 4);
                        break;
                    default:
                        out.write(c);
                        break;
                }
            }

            @Override // java.io.Writer
            public void write(char[] cbuf, int off, int len) throws IOException {
                int lastoff = off;
                int lastpos = off + len;
                for (int i = off; i < lastpos; i++) {
                    switch (cbuf[i]) {
                        case '\"':
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(XmlEscape.QUOT, 0, 6);
                            lastoff = i + 1;
                            break;
                        case '&':
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(XmlEscape.AMP, 0, 5);
                            lastoff = i + 1;
                            break;
                        case '\'':
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(XmlEscape.APOS, 0, 6);
                            lastoff = i + 1;
                            break;
                        case '<':
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(XmlEscape.LT, 0, 4);
                            lastoff = i + 1;
                            break;
                        case '>':
                            out.write(cbuf, lastoff, i - lastoff);
                            out.write(XmlEscape.GT, 0, 4);
                            lastoff = i + 1;
                            break;
                    }
                }
                int remaining = lastpos - lastoff;
                if (remaining > 0) {
                    out.write(cbuf, lastoff, remaining);
                }
            }

            @Override // java.io.Writer, java.io.Flushable
            public void flush() throws IOException {
                out.flush();
            }

            @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
            }
        };
    }
}
