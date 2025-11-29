package freemarker.template.utility;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/StandardCompress.class */
public class StandardCompress implements TemplateTransformModel {
    private static final String BUFFER_SIZE_KEY = "buffer_size";
    private static final String SINGLE_LINE_KEY = "single_line";
    private int defaultBufferSize;
    public static final StandardCompress INSTANCE = new StandardCompress();

    public StandardCompress() {
        this(2048);
    }

    public StandardCompress(int defaultBufferSize) {
        this.defaultBufferSize = defaultBufferSize;
    }

    @Override // freemarker.template.TemplateTransformModel
    public Writer getWriter(Writer out, Map args) throws TemplateModelException {
        int bufferSize = this.defaultBufferSize;
        boolean singleLine = false;
        if (args != null) {
            try {
                TemplateNumberModel num = (TemplateNumberModel) args.get(BUFFER_SIZE_KEY);
                if (num != null) {
                    bufferSize = num.getAsNumber().intValue();
                }
                try {
                    TemplateBooleanModel flag = (TemplateBooleanModel) args.get(SINGLE_LINE_KEY);
                    if (flag != null) {
                        singleLine = flag.getAsBoolean();
                    }
                } catch (ClassCastException e) {
                    throw new TemplateModelException("Expecting boolean argument to single_line");
                }
            } catch (ClassCastException e2) {
                throw new TemplateModelException("Expecting numerical argument to buffer_size");
            }
        }
        return new StandardCompressWriter(out, bufferSize, singleLine);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/StandardCompress$StandardCompressWriter.class */
    private static class StandardCompressWriter extends Writer {
        private static final int MAX_EOL_LENGTH = 2;
        private static final int AT_BEGINNING = 0;
        private static final int SINGLE_LINE = 1;
        private static final int INIT = 2;
        private static final int SAW_CR = 3;
        private static final int LINEBREAK_CR = 4;
        private static final int LINEBREAK_CRLF = 5;
        private static final int LINEBREAK_LF = 6;
        private final Writer out;
        private final char[] buf;
        private final boolean singleLine;
        private int pos = 0;
        private boolean inWhitespace = true;
        private int lineBreakState = 0;

        public StandardCompressWriter(Writer out, int bufSize, boolean singleLine) {
            this.out = out;
            this.singleLine = singleLine;
            this.buf = new char[bufSize];
        }

        @Override // java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            while (true) {
                int room = (this.buf.length - this.pos) - 2;
                if (room >= len) {
                    writeHelper(cbuf, off, len);
                    return;
                } else if (room <= 0) {
                    flushInternal();
                } else {
                    writeHelper(cbuf, off, room);
                    flushInternal();
                    off += room;
                    len -= room;
                }
            }
        }

        private void writeHelper(char[] cbuf, int off, int len) {
            int end = off + len;
            for (int i = off; i < end; i++) {
                char c = cbuf[i];
                if (Character.isWhitespace(c)) {
                    this.inWhitespace = true;
                    updateLineBreakState(c);
                } else if (this.inWhitespace) {
                    this.inWhitespace = false;
                    writeLineBreakOrSpace();
                    char[] cArr = this.buf;
                    int i2 = this.pos;
                    this.pos = i2 + 1;
                    cArr[i2] = c;
                } else {
                    char[] cArr2 = this.buf;
                    int i3 = this.pos;
                    this.pos = i3 + 1;
                    cArr2[i3] = c;
                }
            }
        }

        private void updateLineBreakState(char c) {
            switch (this.lineBreakState) {
                case 2:
                    if (c == '\r') {
                        this.lineBreakState = 3;
                        break;
                    } else if (c == '\n') {
                        this.lineBreakState = 6;
                        break;
                    }
                    break;
                case 3:
                    if (c == '\n') {
                        this.lineBreakState = 5;
                        break;
                    } else {
                        this.lineBreakState = 4;
                        break;
                    }
            }
        }

        private void writeLineBreakOrSpace() {
            switch (this.lineBreakState) {
                case 1:
                case 2:
                    char[] cArr = this.buf;
                    int i = this.pos;
                    this.pos = i + 1;
                    cArr[i] = ' ';
                    break;
                case 3:
                case 4:
                    char[] cArr2 = this.buf;
                    int i2 = this.pos;
                    this.pos = i2 + 1;
                    cArr2[i2] = '\r';
                    break;
                case 5:
                    char[] cArr3 = this.buf;
                    int i3 = this.pos;
                    this.pos = i3 + 1;
                    cArr3[i3] = '\r';
                case 6:
                    char[] cArr4 = this.buf;
                    int i4 = this.pos;
                    this.pos = i4 + 1;
                    cArr4[i4] = '\n';
                    break;
            }
            this.lineBreakState = this.singleLine ? 1 : 2;
        }

        private void flushInternal() throws IOException {
            this.out.write(this.buf, 0, this.pos);
            this.pos = 0;
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
            flushInternal();
            this.out.flush();
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            flushInternal();
        }
    }
}
