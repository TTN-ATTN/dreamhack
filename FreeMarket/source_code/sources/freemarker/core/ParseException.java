package freemarker.core;

import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ParseException.class */
public class ParseException extends IOException implements FMParserConstants {
    private static final String END_TAG_SYNTAX_HINT = "(Note that FreeMarker end-tags must have # or @ after the / character.)";
    public Token currentToken;
    private static volatile Boolean jbossToolsMode;
    private boolean messageAndDescriptionRendered;
    private String message;
    private String description;
    public int columnNumber;
    public int lineNumber;
    public int endColumnNumber;
    public int endLineNumber;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol;

    @Deprecated
    protected boolean specialConstructor;
    private String templateName;

    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super("");
        this.eol = SecurityUtilities.getSystemProperty("line.separator", "\n");
        this.currentToken = currentTokenVal;
        this.specialConstructor = true;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
        this.lineNumber = this.currentToken.next.beginLine;
        this.columnNumber = this.currentToken.next.beginColumn;
        this.endLineNumber = this.currentToken.next.endLine;
        this.endColumnNumber = this.currentToken.next.endColumn;
    }

    @Deprecated
    protected ParseException() {
        this.eol = SecurityUtilities.getSystemProperty("line.separator", "\n");
    }

    @Deprecated
    public ParseException(String description, int lineNumber, int columnNumber) {
        this(description, null, lineNumber, columnNumber, null);
    }

    public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber) {
        this(description, template, lineNumber, columnNumber, endLineNumber, endColumnNumber, (Throwable) null);
    }

    public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), lineNumber, columnNumber, endLineNumber, endColumnNumber, cause);
    }

    @Deprecated
    public ParseException(String description, Template template, int lineNumber, int columnNumber) {
        this(description, template, lineNumber, columnNumber, null);
    }

    @Deprecated
    public ParseException(String description, Template template, int lineNumber, int columnNumber, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), lineNumber, columnNumber, 0, 0, cause);
    }

    public ParseException(String description, Template template, Token tk) {
        this(description, template, tk, (Throwable) null);
    }

    public ParseException(String description, Template template, Token tk, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), tk.beginLine, tk.beginColumn, tk.endLine, tk.endColumn, cause);
    }

    public ParseException(String description, TemplateObject tobj) {
        this(description, tobj, (Throwable) null);
    }

    public ParseException(String description, TemplateObject tobj, Throwable cause) {
        this(description, tobj.getTemplate() == null ? null : tobj.getTemplate().getSourceName(), tobj.beginLine, tobj.beginColumn, tobj.endLine, tobj.endColumn, cause);
    }

    private ParseException(String description, String templateName, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
        super(description);
        this.eol = SecurityUtilities.getSystemProperty("line.separator", "\n");
        try {
            initCause(cause);
        } catch (Exception e) {
        }
        this.description = description;
        this.templateName = templateName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.endLineNumber = endLineNumber;
        this.endColumnNumber = endColumnNumber;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
        synchronized (this) {
            this.messageAndDescriptionRendered = false;
            this.message = null;
        }
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String str;
        synchronized (this) {
            if (this.messageAndDescriptionRendered) {
                return this.message;
            }
            renderMessageAndDescription();
            synchronized (this) {
                str = this.message;
            }
            return str;
        }
    }

    private String getDescription() {
        String str;
        synchronized (this) {
            if (this.messageAndDescriptionRendered) {
                return this.description;
            }
            renderMessageAndDescription();
            synchronized (this) {
                str = this.description;
            }
            return str;
        }
    }

    public String getEditorMessage() {
        return getDescription();
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public int getEndLineNumber() {
        return this.endLineNumber;
    }

    public int getEndColumnNumber() {
        return this.endColumnNumber;
    }

    private void renderMessageAndDescription() {
        String prefix;
        String desc = getOrRenderDescription();
        if (!isInJBossToolsMode()) {
            prefix = "Syntax error " + _MessageUtil.formatLocationForSimpleParsingError(this.templateName, this.lineNumber, this.columnNumber) + ":\n";
        } else {
            prefix = "[col. " + this.columnNumber + "] ";
        }
        String msg = prefix + desc;
        String desc2 = msg.substring(prefix.length());
        synchronized (this) {
            this.message = msg;
            this.description = desc2;
            this.messageAndDescriptionRendered = true;
        }
    }

    private boolean isInJBossToolsMode() {
        if (jbossToolsMode == null) {
            try {
                jbossToolsMode = Boolean.valueOf(ParseException.class.getClassLoader().toString().indexOf("[org.jboss.ide.eclipse.freemarker:") != -1);
            } catch (Throwable th) {
                jbossToolsMode = Boolean.FALSE;
            }
        }
        return jbossToolsMode.booleanValue();
    }

    private String getOrRenderDescription() {
        Set<String> expectedEndTokenDescs;
        synchronized (this) {
            if (this.description != null) {
                return this.description;
            }
            if (this.currentToken == null) {
                return null;
            }
            Token unexpectedTok = this.currentToken.next;
            if (unexpectedTok.kind == 0) {
                Set<String> endTokenDescs = getExpectedEndTokenDescs();
                return "Unexpected end of file reached." + (endTokenDescs.size() == 0 ? "" : " You have an unclosed " + joinWithAnds(endTokenDescs) + ". Check if the FreeMarker end-tags are present, and aren't malformed. " + END_TAG_SYNTAX_HINT);
            }
            int maxExpectedTokenSequenceLength = 0;
            for (int i = 0; i < this.expectedTokenSequences.length; i++) {
                int[] expectedTokenSequence = this.expectedTokenSequences[i];
                if (maxExpectedTokenSequenceLength < expectedTokenSequence.length) {
                    maxExpectedTokenSequenceLength = expectedTokenSequence.length;
                }
            }
            StringBuilder tokenErrDesc = new StringBuilder();
            tokenErrDesc.append("Encountered ");
            boolean encounteredEndTag = false;
            int i2 = 0;
            while (true) {
                if (i2 >= maxExpectedTokenSequenceLength) {
                    break;
                }
                if (i2 != 0) {
                    tokenErrDesc.append(" ");
                }
                if (unexpectedTok.kind == 0) {
                    tokenErrDesc.append(this.tokenImage[0]);
                    break;
                }
                String image = unexpectedTok.image;
                if (i2 == 0 && (image.startsWith("</") || image.startsWith("[/"))) {
                    encounteredEndTag = true;
                }
                tokenErrDesc.append(StringUtil.jQuote(image));
                unexpectedTok = unexpectedTok.next;
                i2++;
            }
            int unexpTokKind = this.currentToken.next.kind;
            if (getIsEndToken(unexpTokKind) || unexpTokKind == 54 || unexpTokKind == 9) {
                expectedEndTokenDescs = new LinkedHashSet<>(getExpectedEndTokenDescs());
                if (unexpTokKind == 54 || unexpTokKind == 9) {
                    expectedEndTokenDescs.remove(getEndTokenDescIfIsEndToken(36));
                } else {
                    expectedEndTokenDescs.remove(getEndTokenDescIfIsEndToken(unexpTokKind));
                }
            } else {
                expectedEndTokenDescs = Collections.emptySet();
            }
            if (!expectedEndTokenDescs.isEmpty()) {
                if (unexpTokKind == 54 || unexpTokKind == 9) {
                    tokenErrDesc.append(", which can only be used where an #if");
                    if (unexpTokKind == 54) {
                        tokenErrDesc.append(" or #list");
                    }
                    tokenErrDesc.append(" could be closed");
                }
                tokenErrDesc.append(", but at this place only ");
                tokenErrDesc.append(expectedEndTokenDescs.size() > 1 ? "these" : "this");
                tokenErrDesc.append(" can be closed: ");
                boolean first = true;
                for (String expectedEndTokenDesc : expectedEndTokenDescs) {
                    if (!first) {
                        tokenErrDesc.append(", ");
                    } else {
                        first = false;
                    }
                    tokenErrDesc.append(!expectedEndTokenDesc.startsWith("\"") ? StringUtil.jQuote(expectedEndTokenDesc) : expectedEndTokenDesc);
                }
                tokenErrDesc.append(".");
                if (encounteredEndTag) {
                    tokenErrDesc.append(" This usually because of wrong nesting of FreeMarker directives, like a missed or malformed end-tag somewhere. (Note that FreeMarker end-tags must have # or @ after the / character.)");
                }
                tokenErrDesc.append(this.eol);
                tokenErrDesc.append("Was ");
            } else {
                tokenErrDesc.append(", but was ");
            }
            if (this.expectedTokenSequences.length == 1) {
                tokenErrDesc.append("expecting pattern:");
            } else {
                tokenErrDesc.append("expecting one of these patterns:");
            }
            tokenErrDesc.append(this.eol);
            for (int i3 = 0; i3 < this.expectedTokenSequences.length; i3++) {
                if (i3 != 0) {
                    tokenErrDesc.append(this.eol);
                }
                tokenErrDesc.append("    ");
                int[] expectedTokenSequence2 = this.expectedTokenSequences[i3];
                for (int j = 0; j < expectedTokenSequence2.length; j++) {
                    if (j != 0) {
                        tokenErrDesc.append(' ');
                    }
                    tokenErrDesc.append(this.tokenImage[expectedTokenSequence2[j]]);
                }
            }
            return tokenErrDesc.toString();
        }
    }

    private Set<String> getExpectedEndTokenDescs() {
        Set<String> endTokenDescs = new LinkedHashSet<>();
        for (int i = 0; i < this.expectedTokenSequences.length; i++) {
            int[] sequence = this.expectedTokenSequences[i];
            for (int token : sequence) {
                String endTokenDesc = getEndTokenDescIfIsEndToken(token);
                if (endTokenDesc != null) {
                    endTokenDescs.add(endTokenDesc);
                }
            }
        }
        return endTokenDescs;
    }

    private boolean getIsEndToken(int token) {
        return getEndTokenDescIfIsEndToken(token) != null;
    }

    private String getEndTokenDescIfIsEndToken(int token) {
        String endTokenDesc = null;
        switch (token) {
            case 36:
                endTokenDesc = "#if";
                break;
            case 37:
                endTokenDesc = "#list";
                break;
            case 38:
                endTokenDesc = "#items";
                break;
            case 39:
                endTokenDesc = "#sep";
                break;
            case 41:
                endTokenDesc = "#attempt";
                break;
            case 42:
                endTokenDesc = "#foreach";
                break;
            case 43:
            case 44:
            case 45:
                endTokenDesc = "#assign or #local or #global";
                break;
            case 46:
            case 47:
                endTokenDesc = "#macro or #function";
                break;
            case 51:
                endTokenDesc = "#compress";
                break;
            case 52:
                endTokenDesc = "#transform";
                break;
            case 53:
                endTokenDesc = "#switch";
                break;
            case 71:
                endTokenDesc = "#escape";
                break;
            case 73:
                endTokenDesc = "#noescape";
                break;
            case 75:
                endTokenDesc = "@...";
                break;
            case 134:
                endTokenDesc = "\"[\"";
                break;
            case 136:
                endTokenDesc = "\"(\"";
                break;
            case 138:
                endTokenDesc = "\"{\"";
                break;
        }
        return endTokenDesc;
    }

    private String joinWithAnds(Collection<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (sb.length() != 0) {
                sb.append(" and ");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    protected String add_escapes(String str) {
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
}
