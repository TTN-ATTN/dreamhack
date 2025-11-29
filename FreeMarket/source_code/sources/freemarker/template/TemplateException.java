package freemarker.template;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.core._CoreAPI;
import freemarker.core._ErrorDescriptionBuilder;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateException.class */
public class TemplateException extends Exception {
    private static final String FTL_INSTRUCTION_STACK_TRACE_TITLE = "FTL stack trace (\"~\" means nesting-related):";
    private transient _ErrorDescriptionBuilder descriptionBuilder;
    private final transient Environment env;
    private final transient Expression blamedExpression;
    private transient TemplateElement[] ftlInstructionStackSnapshot;
    private String renderedFtlInstructionStackSnapshot;
    private String renderedFtlInstructionStackSnapshotTop;
    private String description;
    private transient String messageWithoutStackTop;
    private transient String message;
    private boolean blamedExpressionStringCalculated;
    private String blamedExpressionString;
    private boolean positionsCalculated;
    private String templateName;
    private String templateSourceName;
    private Integer lineNumber;
    private Integer columnNumber;
    private Integer endLineNumber;
    private Integer endColumnNumber;
    private transient Object lock;
    private transient ThreadLocal messageWasAlreadyPrintedForThisTrace;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateException$StackTraceWriter.class */
    private interface StackTraceWriter {
        void print(Object obj);

        void println(Object obj);

        void println();

        void printStandardStackTrace(Throwable th);
    }

    public TemplateException(Environment env) {
        this((String) null, (Exception) null, env);
    }

    public TemplateException(String description, Environment env) {
        this(description, (Exception) null, env);
    }

    public TemplateException(Exception cause, Environment env) {
        this((String) null, cause, env);
    }

    public TemplateException(Throwable cause, Environment env) {
        this((String) null, cause, env);
    }

    public TemplateException(String description, Exception cause, Environment env) {
        this(description, cause, env, null, null);
    }

    public TemplateException(String description, Throwable cause, Environment env) {
        this(description, cause, env, null, null);
    }

    protected TemplateException(Throwable cause, Environment env, Expression blamedExpr, _ErrorDescriptionBuilder descriptionBuilder) {
        this(null, cause, env, blamedExpr, descriptionBuilder);
    }

    private TemplateException(String renderedDescription, Throwable cause, Environment env, Expression blamedExpression, _ErrorDescriptionBuilder descriptionBuilder) {
        super(cause);
        this.lock = new Object();
        env = env == null ? Environment.getCurrentEnvironment() : env;
        this.env = env;
        this.blamedExpression = blamedExpression;
        this.descriptionBuilder = descriptionBuilder;
        this.description = renderedDescription;
        if (env != null) {
            this.ftlInstructionStackSnapshot = _CoreAPI.getInstructionStackSnapshot(env);
        }
    }

    private void renderMessages() {
        String description = getDescription();
        if (description != null && description.length() != 0) {
            this.messageWithoutStackTop = description;
        } else if (getCause() != null) {
            this.messageWithoutStackTop = "No error description was specified for this error; low-level message: " + getCause().getClass().getName() + ": " + getCause().getMessage();
        } else {
            this.messageWithoutStackTop = "[No error description was available.]";
        }
        String stackTopFew = getFTLInstructionStackTopFew();
        if (stackTopFew != null) {
            this.message = this.messageWithoutStackTop + "\n\n" + _CoreAPI.ERROR_MESSAGE_HR + "\n" + FTL_INSTRUCTION_STACK_TRACE_TITLE + "\n" + stackTopFew + _CoreAPI.ERROR_MESSAGE_HR;
            this.messageWithoutStackTop = this.message.substring(0, this.messageWithoutStackTop.length());
        } else {
            this.message = this.messageWithoutStackTop;
        }
    }

    private void calculatePosition() {
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                TemplateObject templateObject = this.blamedExpression != null ? this.blamedExpression : (this.ftlInstructionStackSnapshot == null || this.ftlInstructionStackSnapshot.length == 0) ? null : this.ftlInstructionStackSnapshot[0];
                if (templateObject != null && templateObject.getBeginLine() > 0) {
                    Template template = templateObject.getTemplate();
                    this.templateName = template != null ? template.getName() : null;
                    this.templateSourceName = template != null ? template.getSourceName() : null;
                    this.lineNumber = Integer.valueOf(templateObject.getBeginLine());
                    this.columnNumber = Integer.valueOf(templateObject.getBeginColumn());
                    this.endLineNumber = Integer.valueOf(templateObject.getEndLine());
                    this.endColumnNumber = Integer.valueOf(templateObject.getEndColumn());
                }
                this.positionsCalculated = true;
                deleteFTLInstructionStackSnapshotIfNotNeeded();
            }
        }
    }

    @Deprecated
    public Exception getCauseException() {
        if (getCause() instanceof Exception) {
            return (Exception) getCause();
        }
        return new Exception("Wrapped to Exception: " + getCause(), getCause());
    }

    public String getFTLInstructionStack() {
        synchronized (this.lock) {
            if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshot != null) {
                if (this.renderedFtlInstructionStackSnapshot == null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, false, pw);
                    pw.close();
                    if (this.renderedFtlInstructionStackSnapshot == null) {
                        this.renderedFtlInstructionStackSnapshot = sw.toString();
                        deleteFTLInstructionStackSnapshotIfNotNeeded();
                    }
                }
                return this.renderedFtlInstructionStackSnapshot;
            }
            return null;
        }
    }

    private String getFTLInstructionStackTopFew() {
        String s;
        synchronized (this.lock) {
            if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshotTop != null) {
                if (this.renderedFtlInstructionStackSnapshotTop == null) {
                    int stackSize = this.ftlInstructionStackSnapshot.length;
                    if (stackSize == 0) {
                        s = "";
                    } else {
                        StringWriter sw = new StringWriter();
                        _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, true, sw);
                        s = sw.toString();
                    }
                    if (this.renderedFtlInstructionStackSnapshotTop == null) {
                        this.renderedFtlInstructionStackSnapshotTop = s;
                        deleteFTLInstructionStackSnapshotIfNotNeeded();
                    }
                }
                return this.renderedFtlInstructionStackSnapshotTop.length() != 0 ? this.renderedFtlInstructionStackSnapshotTop : null;
            }
            return null;
        }
    }

    private void deleteFTLInstructionStackSnapshotIfNotNeeded() {
        if (this.renderedFtlInstructionStackSnapshot == null || this.renderedFtlInstructionStackSnapshotTop == null) {
            return;
        }
        if (this.positionsCalculated || this.blamedExpression != null) {
            this.ftlInstructionStackSnapshot = null;
        }
    }

    private String getDescription() {
        String str;
        synchronized (this.lock) {
            if (this.description == null && this.descriptionBuilder != null) {
                this.description = this.descriptionBuilder.toString(getFailingInstruction(), this.env != null ? this.env.getShowErrorTips() : true);
                this.descriptionBuilder = null;
            }
            str = this.description;
        }
        return str;
    }

    private TemplateElement getFailingInstruction() {
        if (this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length > 0) {
            return this.ftlInstructionStackSnapshot[0];
        }
        return null;
    }

    public Environment getEnvironment() {
        return this.env;
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream out) {
        printStackTrace(out, true, true, true);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter out) {
        printStackTrace(out, true, true, true);
    }

    public void printStackTrace(PrintWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
        synchronized (out) {
            printStackTrace(new PrintWriterStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
        }
    }

    public void printStackTrace(PrintStream out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
        synchronized (out) {
            printStackTrace(new PrintStreamStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0015 A[Catch: all -> 0x012d, TryCatch #3 {, blocks: (B:6:0x0009, B:9:0x0015, B:11:0x0020, B:17:0x0061, B:18:0x007e, B:19:0x007f, B:21:0x0086, B:22:0x0091, B:23:0x009d, B:30:0x00a9, B:31:0x00b1, B:38:0x00d6, B:40:0x00dd, B:42:0x00eb, B:44:0x0112, B:34:0x00c0, B:35:0x00cb, B:27:0x00a5, B:29:0x00a8, B:37:0x00cf, B:48:0x0129), top: B:62:0x0009, inners: #0, #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void printStackTrace(freemarker.template.TemplateException.StackTraceWriter r5, boolean r6, boolean r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 310
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.TemplateException.printStackTrace(freemarker.template.TemplateException$StackTraceWriter, boolean, boolean, boolean):void");
    }

    public void printStandardStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
    }

    public void printStandardStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String str;
        if (this.messageWasAlreadyPrintedForThisTrace != null && this.messageWasAlreadyPrintedForThisTrace.get() == Boolean.TRUE) {
            return "[... Exception message was already printed; see it above ...]";
        }
        synchronized (this.lock) {
            if (this.message == null) {
                renderMessages();
            }
            str = this.message;
        }
        return str;
    }

    public String getMessageWithoutStackTop() {
        String str;
        synchronized (this.lock) {
            if (this.messageWithoutStackTop == null) {
                renderMessages();
            }
            str = this.messageWithoutStackTop;
        }
        return str;
    }

    public Integer getLineNumber() {
        Integer num;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            num = this.lineNumber;
        }
        return num;
    }

    @Deprecated
    public String getTemplateName() {
        String str;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            str = this.templateName;
        }
        return str;
    }

    public String getTemplateSourceName() {
        String str;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            str = this.templateSourceName;
        }
        return str;
    }

    public Integer getColumnNumber() {
        Integer num;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            num = this.columnNumber;
        }
        return num;
    }

    public Integer getEndLineNumber() {
        Integer num;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            num = this.endLineNumber;
        }
        return num;
    }

    public Integer getEndColumnNumber() {
        Integer num;
        synchronized (this.lock) {
            if (!this.positionsCalculated) {
                calculatePosition();
            }
            num = this.endColumnNumber;
        }
        return num;
    }

    public String getBlamedExpressionString() {
        String str;
        synchronized (this.lock) {
            if (!this.blamedExpressionStringCalculated) {
                if (this.blamedExpression != null) {
                    this.blamedExpressionString = this.blamedExpression.getCanonicalForm();
                }
                this.blamedExpressionStringCalculated = true;
            }
            str = this.blamedExpressionString;
        }
        return str;
    }

    Expression getBlamedExpression() {
        return this.blamedExpression;
    }

    private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        getFTLInstructionStack();
        getFTLInstructionStackTopFew();
        getDescription();
        calculatePosition();
        getBlamedExpressionString();
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        this.lock = new Object();
        in.defaultReadObject();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateException$PrintStreamStackTraceWriter.class */
    private static class PrintStreamStackTraceWriter implements StackTraceWriter {
        private final PrintStream out;

        PrintStreamStackTraceWriter(PrintStream out) {
            this.out = out;
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void print(Object obj) {
            this.out.print(obj);
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void println(Object obj) {
            this.out.println(obj);
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void println() {
            this.out.println();
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void printStandardStackTrace(Throwable exception) {
            if (exception instanceof TemplateException) {
                ((TemplateException) exception).printStandardStackTrace(this.out);
            } else {
                exception.printStackTrace(this.out);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateException$PrintWriterStackTraceWriter.class */
    private static class PrintWriterStackTraceWriter implements StackTraceWriter {
        private final PrintWriter out;

        PrintWriterStackTraceWriter(PrintWriter out) {
            this.out = out;
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void print(Object obj) {
            this.out.print(obj);
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void println(Object obj) {
            this.out.println(obj);
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void println() {
            this.out.println();
        }

        @Override // freemarker.template.TemplateException.StackTraceWriter
        public void printStandardStackTrace(Throwable exception) {
            if (exception instanceof TemplateException) {
                ((TemplateException) exception).printStandardStackTrace(this.out);
            } else {
                exception.printStackTrace(this.out);
            }
        }
    }
}
