package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TextBlock.class */
public final class TextBlock extends TemplateElement {
    private char[] text;
    private final boolean unparsed;

    public TextBlock(String text) {
        this(text, false);
    }

    public TextBlock(String text, boolean unparsed) {
        this(text.toCharArray(), unparsed);
    }

    TextBlock(char[] text, boolean unparsed) {
        this.text = text;
        this.unparsed = unparsed;
    }

    void replaceText(String text) {
        this.text = text.toCharArray();
    }

    @Override // freemarker.core.TemplateElement
    public TemplateElement[] accept(Environment env) throws IOException {
        env.getOut().write(this.text);
        return null;
    }

    @Override // freemarker.core.TemplateElement
    protected String dump(boolean canonical) {
        if (canonical) {
            String text = new String(this.text);
            if (this.unparsed) {
                return "<#noparse>" + text + "</#noparse>";
            }
            return text;
        }
        return "text " + StringUtil.jQuote(new String(this.text));
    }

    @Override // freemarker.core.TemplateObject
    String getNodeTypeSymbol() {
        return "#text";
    }

    @Override // freemarker.core.TemplateObject
    int getParameterCount() {
        return 1;
    }

    @Override // freemarker.core.TemplateObject
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this.text);
    }

    @Override // freemarker.core.TemplateObject
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    @Override // freemarker.core.TemplateElement
    TemplateElement postParseCleanup(boolean stripWhitespace) {
        if (this.text.length == 0) {
            return this;
        }
        int openingCharsToStrip = 0;
        int trailingCharsToStrip = 0;
        boolean deliberateLeftTrim = deliberateLeftTrim();
        boolean deliberateRightTrim = deliberateRightTrim();
        if (!stripWhitespace || this.text.length == 0) {
            return this;
        }
        TemplateElement parentElement = getParentElement();
        if (isTopLevelTextIfParentIs(parentElement) && previousSibling() == null) {
            return this;
        }
        if (!deliberateLeftTrim) {
            trailingCharsToStrip = trailingCharsToStrip();
        }
        if (!deliberateRightTrim) {
            openingCharsToStrip = openingCharsToStrip();
        }
        if (openingCharsToStrip == 0 && trailingCharsToStrip == 0) {
            return this;
        }
        this.text = substring(this.text, openingCharsToStrip, this.text.length - trailingCharsToStrip);
        if (openingCharsToStrip > 0) {
            this.beginLine++;
            this.beginColumn = 1;
        }
        if (trailingCharsToStrip > 0) {
            this.endColumn = 0;
        }
        return this;
    }

    private boolean deliberateLeftTrim() {
        boolean result = false;
        TemplateElement templateElementNextTerminalNode = nextTerminalNode();
        while (true) {
            TemplateElement elem = templateElementNextTerminalNode;
            if (elem == null || elem.beginLine != this.endLine) {
                break;
            }
            if (elem instanceof TrimInstruction) {
                TrimInstruction ti = (TrimInstruction) elem;
                if (!ti.left && !ti.right) {
                    result = true;
                }
                if (ti.left) {
                    result = true;
                    int lastNewLineIndex = lastNewLineIndex();
                    if (lastNewLineIndex >= 0 || this.beginColumn == 1) {
                        char[] firstPart = substring(this.text, 0, lastNewLineIndex + 1);
                        char[] lastLine = substring(this.text, 1 + lastNewLineIndex);
                        if (StringUtil.isTrimmableToEmpty(lastLine)) {
                            this.text = firstPart;
                            this.endColumn = 0;
                        } else {
                            int i = 0;
                            while (Character.isWhitespace(lastLine[i])) {
                                i++;
                            }
                            char[] printablePart = substring(lastLine, i);
                            this.text = concat(firstPart, printablePart);
                        }
                    }
                }
            }
            templateElementNextTerminalNode = elem.nextTerminalNode();
        }
        return result;
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x013e, code lost:
    
        return r6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean deliberateRightTrim() {
        /*
            Method dump skipped, instructions count: 319
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.TextBlock.deliberateRightTrim():boolean");
    }

    private int firstNewLineIndex() {
        char[] text = this.text;
        for (int i = 0; i < text.length; i++) {
            char c = text[i];
            if (c == '\r' || c == '\n') {
                return i;
            }
        }
        return -1;
    }

    private int lastNewLineIndex() {
        char[] text = this.text;
        for (int i = text.length - 1; i >= 0; i--) {
            char c = text[i];
            if (c == '\r' || c == '\n') {
                return i;
            }
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x0073, code lost:
    
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int openingCharsToStrip() {
        /*
            r4 = this;
            r0 = r4
            int r0 = r0.firstNewLineIndex()
            r5 = r0
            r0 = r5
            r1 = -1
            if (r0 != r1) goto L14
            r0 = r4
            int r0 = r0.beginColumn
            r1 = 1
            if (r0 == r1) goto L14
            r0 = 0
            return r0
        L14:
            int r5 = r5 + 1
            r0 = r4
            char[] r0 = r0.text
            int r0 = r0.length
            r1 = r5
            if (r0 <= r1) goto L3f
            r0 = r5
            if (r0 <= 0) goto L3f
            r0 = r4
            char[] r0 = r0.text
            r1 = r5
            r2 = 1
            int r1 = r1 - r2
            char r0 = r0[r1]
            r1 = 13
            if (r0 != r1) goto L3f
            r0 = r4
            char[] r0 = r0.text
            r1 = r5
            char r0 = r0[r1]
            r1 = 10
            if (r0 != r1) goto L3f
            int r5 = r5 + 1
        L3f:
            r0 = r4
            char[] r0 = r0.text
            r1 = 0
            r2 = r5
            boolean r0 = freemarker.template.utility.StringUtil.isTrimmableToEmpty(r0, r1, r2)
            if (r0 != 0) goto L4d
            r0 = 0
            return r0
        L4d:
            r0 = r4
            freemarker.core.TemplateElement r0 = r0.prevTerminalNode()
            r6 = r0
        L52:
            r0 = r6
            if (r0 == 0) goto L72
            r0 = r6
            int r0 = r0.endLine
            r1 = r4
            int r1 = r1.beginLine
            if (r0 != r1) goto L72
            r0 = r6
            boolean r0 = r0.heedsOpeningWhitespace()
            if (r0 == 0) goto L6a
            r0 = 0
            return r0
        L6a:
            r0 = r6
            freemarker.core.TemplateElement r0 = r0.prevTerminalNode()
            r6 = r0
            goto L52
        L72:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.TextBlock.openingCharsToStrip():int");
    }

    private int trailingCharsToStrip() {
        int lastNewlineIndex = lastNewLineIndex();
        if ((lastNewlineIndex == -1 && this.beginColumn != 1) || !StringUtil.isTrimmableToEmpty(this.text, lastNewlineIndex + 1)) {
            return 0;
        }
        TemplateElement templateElementNextTerminalNode = nextTerminalNode();
        while (true) {
            TemplateElement elem = templateElementNextTerminalNode;
            if (elem == null || elem.beginLine != this.endLine) {
                break;
            }
            if (!elem.heedsTrailingWhitespace()) {
                templateElementNextTerminalNode = elem.nextTerminalNode();
            } else {
                return 0;
            }
        }
        return this.text.length - (lastNewlineIndex + 1);
    }

    @Override // freemarker.core.TemplateElement
    boolean heedsTrailingWhitespace() {
        if (isIgnorable(true)) {
            return false;
        }
        for (int i = 0; i < this.text.length; i++) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean heedsOpeningWhitespace() {
        if (isIgnorable(true)) {
            return false;
        }
        for (int i = this.text.length - 1; i >= 0; i--) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isIgnorable(boolean stripWhitespace) {
        if (this.text == null || this.text.length == 0) {
            return true;
        }
        if (!stripWhitespace || !StringUtil.isTrimmableToEmpty(this.text)) {
            return false;
        }
        TemplateElement parentElement = getParentElement();
        boolean atTopLevel = isTopLevelTextIfParentIs(parentElement);
        TemplateElement prevSibling = previousSibling();
        TemplateElement nextSibling = nextSibling();
        return ((prevSibling == null && atTopLevel) || nonOutputtingType(prevSibling)) && ((nextSibling == null && atTopLevel) || nonOutputtingType(nextSibling));
    }

    private boolean isTopLevelTextIfParentIs(TemplateElement parentElement) {
        return parentElement == null || (parentElement.getParentElement() == null && (parentElement instanceof MixedContent));
    }

    private boolean nonOutputtingType(TemplateElement element) {
        return (element instanceof Macro) || (element instanceof Assignment) || (element instanceof AssignmentInstruction) || (element instanceof PropertySetting) || (element instanceof LibraryLoad) || (element instanceof Comment);
    }

    private static char[] substring(char[] c, int from, int to) {
        char[] c2 = new char[to - from];
        System.arraycopy(c, from, c2, 0, c2.length);
        return c2;
    }

    private static char[] substring(char[] c, int from) {
        return substring(c, from, c.length);
    }

    private static char[] concat(char[] c1, char[] c2) {
        char[] c = new char[c1.length + c2.length];
        System.arraycopy(c1, 0, c, 0, c1.length);
        System.arraycopy(c2, 0, c, c1.length, c2.length);
        return c;
    }

    @Override // freemarker.core.TemplateElement
    boolean isOutputCacheable() {
        return true;
    }

    @Override // freemarker.core.TemplateElement
    boolean isNestedBlockRepeater() {
        return false;
    }
}
