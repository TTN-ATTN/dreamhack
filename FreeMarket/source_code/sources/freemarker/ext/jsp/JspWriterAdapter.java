package freemarker.ext.jsp;

import freemarker.template.utility.SecurityUtilities;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspWriter;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jsp/JspWriterAdapter.class */
class JspWriterAdapter extends JspWriter {
    static final char[] NEWLINE = SecurityUtilities.getSystemProperty("line.separator", "\n").toCharArray();
    private final Writer out;

    JspWriterAdapter(Writer out) {
        super(0, true);
        this.out = out;
    }

    public String toString() {
        return "JspWriterAdapter wrapping a " + this.out.toString();
    }

    public void clear() throws IOException {
        throw new IOException("Can't clear");
    }

    public void clearBuffer() throws IOException {
        throw new IOException("Can't clear");
    }

    public void close() throws IOException {
        throw new IOException("Close not permitted.");
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public int getRemaining() {
        return 0;
    }

    public void newLine() throws IOException {
        this.out.write(NEWLINE);
    }

    public void print(boolean arg0) throws IOException {
        this.out.write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    }

    public void print(char arg0) throws IOException {
        this.out.write(arg0);
    }

    public void print(char[] arg0) throws IOException {
        this.out.write(arg0);
    }

    public void print(double arg0) throws IOException {
        this.out.write(Double.toString(arg0));
    }

    public void print(float arg0) throws IOException {
        this.out.write(Float.toString(arg0));
    }

    public void print(int arg0) throws IOException {
        this.out.write(Integer.toString(arg0));
    }

    public void print(long arg0) throws IOException {
        this.out.write(Long.toString(arg0));
    }

    public void print(Object arg0) throws IOException {
        this.out.write(arg0 == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : arg0.toString());
    }

    public void print(String arg0) throws IOException {
        this.out.write(arg0);
    }

    public void println() throws IOException {
        newLine();
    }

    public void println(boolean arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(char arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(char[] arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(double arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(float arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(int arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(long arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(Object arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void println(String arg0) throws IOException {
        print(arg0);
        newLine();
    }

    public void write(int c) throws IOException {
        this.out.write(c);
    }

    public void write(char[] arg0, int arg1, int arg2) throws IOException {
        this.out.write(arg0, arg1, arg2);
    }
}
