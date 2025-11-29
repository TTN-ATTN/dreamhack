package freemarker.core;

import freemarker.template.Template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TokenMgrError.class */
public class TokenMgrError extends Error {
    static final int LEXICAL_ERROR = 0;
    static final int STATIC_LEXER_ERROR = 1;
    static final int INVALID_LEXICAL_STATE = 2;
    static final int LOOP_DETECTED = 3;
    int errorCode;
    private String detail;
    private Integer lineNumber;
    private Integer columnNumber;
    private Integer endLineNumber;
    private Integer endColumnNumber;

    protected static final String addEscapes(String str) {
        StringBuilder retval = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case 0:
                    break;
                case '\b':
                    retval.append("\\b");
                    break;
                case '\t':
                    retval.append("\\t");
                    break;
                case '\n':
                    retval.append("\\n");
                    break;
                case '\f':
                    retval.append("\\f");
                    break;
                case '\r':
                    retval.append("\\r");
                    break;
                case '\"':
                    retval.append("\\\"");
                    break;
                case '\'':
                    retval.append("\\'");
                    break;
                case '\\':
                    retval.append("\\\\");
                    break;
                default:
                    char ch2 = str.charAt(i);
                    if (ch2 < ' ' || ch2 > '~') {
                        String s = "0000" + Integer.toString(ch2, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                        break;
                    } else {
                        retval.append(ch2);
                        break;
                    }
                    break;
            }
        }
        return retval.toString();
    }

    protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
        return "Lexical error: encountered " + (EOFSeen ? "<EOF> " : "\"" + addEscapes(String.valueOf(curChar)) + "\" (" + ((int) curChar) + "), ") + "after \"" + addEscapes(errorAfter) + "\".";
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage();
    }

    public TokenMgrError() {
    }

    public TokenMgrError(String detail, int reason) {
        super(detail);
        this.detail = detail;
        this.errorCode = reason;
    }

    @Deprecated
    public TokenMgrError(String detail, int reason, int errorLine, int errorColumn) {
        this(detail, reason, errorLine, errorColumn, 0, 0);
        this.endLineNumber = null;
        this.endColumnNumber = null;
    }

    public TokenMgrError(String detail, int reason, int errorLine, int errorColumn, int endLineNumber, int endColumnNumber) {
        super(detail);
        this.detail = detail;
        this.errorCode = reason;
        this.lineNumber = Integer.valueOf(errorLine);
        this.columnNumber = Integer.valueOf(errorColumn);
        this.endLineNumber = Integer.valueOf(endLineNumber);
        this.endColumnNumber = Integer.valueOf(endColumnNumber);
    }

    TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, int curChar, int reason) {
        this(EOFSeen, lexState, errorLine, errorColumn, errorAfter, (char) curChar, reason);
    }

    public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
        this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
        this.lineNumber = Integer.valueOf(errorLine);
        this.columnNumber = Integer.valueOf(errorColumn);
        this.endLineNumber = this.lineNumber;
        this.endColumnNumber = this.columnNumber;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    public Integer getColumnNumber() {
        return this.columnNumber;
    }

    public Integer getEndLineNumber() {
        return this.endLineNumber;
    }

    public Integer getEndColumnNumber() {
        return this.endColumnNumber;
    }

    public String getDetail() {
        return this.detail;
    }

    public ParseException toParseException(Template template) {
        return new ParseException(getDetail(), template, getLineNumber() != null ? getLineNumber().intValue() : 0, getColumnNumber() != null ? getColumnNumber().intValue() : 0, getEndLineNumber() != null ? getEndLineNumber().intValue() : 0, getEndColumnNumber() != null ? getEndColumnNumber().intValue() : 0);
    }
}
