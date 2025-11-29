package freemarker.template.utility;

import java.io.IOException;
import java.io.Writer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/NullWriter.class */
public final class NullWriter extends Writer {
    public static final NullWriter INSTANCE = new NullWriter();

    private NullWriter() {
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq) throws IOException {
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(char c) throws IOException {
        return this;
    }
}
