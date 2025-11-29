package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DefaultTruncateBuiltinAlgorithm.class */
public class DefaultTruncateBuiltinAlgorithm extends TruncateBuiltinAlgorithm {
    public static final String STANDARD_ASCII_TERMINATOR = "[...]";
    public static final String STANDARD_UNICODE_TERMINATOR = "[…]";
    public static final TemplateHTMLOutputModel STANDARD_M_TERMINATOR;
    public static final double DEFAULT_WORD_BOUNDARY_MIN_LENGTH = 0.75d;
    private static final int FALLBACK_M_TERMINATOR_LENGTH = 3;
    public static final DefaultTruncateBuiltinAlgorithm ASCII_INSTANCE;
    public static final DefaultTruncateBuiltinAlgorithm UNICODE_INSTANCE;
    private final TemplateScalarModel defaultTerminator;
    private final int defaultTerminatorLength;
    private final boolean defaultTerminatorRemovesDots;
    private final TemplateMarkupOutputModel<?> defaultMTerminator;
    private final Integer defaultMTerminatorLength;
    private final boolean defaultMTerminatorRemovesDots;
    private final double wordBoundaryMinLength;
    private final boolean addSpaceAtWordBoundary;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DefaultTruncateBuiltinAlgorithm$TruncationMode.class */
    private enum TruncationMode {
        CHAR_BOUNDARY,
        WORD_BOUNDARY,
        AUTO
    }

    static {
        try {
            STANDARD_M_TERMINATOR = HTMLOutputFormat.INSTANCE.fromMarkup("<span class='truncateTerminator'>[&#8230;]</span>");
            ASCII_INSTANCE = new DefaultTruncateBuiltinAlgorithm(STANDARD_ASCII_TERMINATOR, STANDARD_M_TERMINATOR, true);
            UNICODE_INSTANCE = new DefaultTruncateBuiltinAlgorithm(STANDARD_UNICODE_TERMINATOR, STANDARD_M_TERMINATOR, true);
        } catch (TemplateModelException e) {
            throw new IllegalStateException(e);
        }
    }

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, TemplateMarkupOutputModel<?> defaultMTerminator, boolean addSpaceAtWordBoundary) {
        this(defaultTerminator, null, null, defaultMTerminator, null, null, addSpaceAtWordBoundary, null);
    }

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, boolean addSpaceAtWordBoundary) {
        this(defaultTerminator, null, null, null, null, null, addSpaceAtWordBoundary, null);
    }

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, Integer defaultTerminatorLength, Boolean defaultTerminatorRemovesDots, TemplateMarkupOutputModel<?> defaultMTerminator, Integer defaultMTerminatorLength, Boolean defaultMTerminatorRemovesDots, boolean addSpaceAtWordBoundary, Double wordBoundaryMinLength) {
        boolean mTerminatorRemovesDots;
        NullArgumentException.check("defaultTerminator", defaultTerminator);
        this.defaultTerminator = new SimpleScalar(defaultTerminator);
        try {
            this.defaultTerminatorLength = defaultTerminatorLength != null ? defaultTerminatorLength.intValue() : defaultTerminator.length();
            this.defaultTerminatorRemovesDots = defaultTerminatorRemovesDots != null ? defaultTerminatorRemovesDots.booleanValue() : getTerminatorRemovesDots(defaultTerminator);
            this.defaultMTerminator = defaultMTerminator;
            if (defaultMTerminator != null) {
                try {
                    this.defaultMTerminatorLength = Integer.valueOf(defaultMTerminatorLength != null ? defaultMTerminatorLength.intValue() : getMTerminatorLength(defaultMTerminator));
                    if (defaultMTerminatorRemovesDots != null) {
                        mTerminatorRemovesDots = defaultMTerminatorRemovesDots.booleanValue();
                    } else {
                        mTerminatorRemovesDots = getMTerminatorRemovesDots(defaultMTerminator);
                    }
                    this.defaultMTerminatorRemovesDots = mTerminatorRemovesDots;
                } catch (TemplateModelException e) {
                    throw new IllegalArgumentException("Failed to examine defaultMTerminator", e);
                }
            } else {
                this.defaultMTerminatorLength = null;
                this.defaultMTerminatorRemovesDots = false;
            }
            if (wordBoundaryMinLength == null) {
                wordBoundaryMinLength = Double.valueOf(0.75d);
            } else if (wordBoundaryMinLength.doubleValue() < 0.0d || wordBoundaryMinLength.doubleValue() > 1.0d) {
                throw new IllegalArgumentException("wordBoundaryMinLength must be between 0.0 and 1.0 (inclusive)");
            }
            this.wordBoundaryMinLength = wordBoundaryMinLength.doubleValue();
            this.addSpaceAtWordBoundary = addSpaceAtWordBoundary;
        } catch (TemplateModelException e2) {
            throw new IllegalArgumentException("Failed to examine defaultTerminator", e2);
        }
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateScalarModel truncate(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel) unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.AUTO, false);
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateScalarModel truncateW(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel) unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, false);
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateScalarModel truncateC(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel) unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, false);
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateModel truncateM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.AUTO, true);
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateModel truncateWM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, true);
    }

    @Override // freemarker.core.TruncateBuiltinAlgorithm
    public TemplateModel truncateCM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, true);
    }

    public String getDefaultTerminator() {
        try {
            return this.defaultTerminator.getAsString();
        } catch (TemplateModelException e) {
            throw new IllegalStateException(e);
        }
    }

    public int getDefaultTerminatorLength() {
        return this.defaultTerminatorLength;
    }

    public boolean getDefaultTerminatorRemovesDots() {
        return this.defaultTerminatorRemovesDots;
    }

    public TemplateMarkupOutputModel<?> getDefaultMTerminator() {
        return this.defaultMTerminator;
    }

    public Integer getDefaultMTerminatorLength() {
        return this.defaultMTerminatorLength;
    }

    public boolean getDefaultMTerminatorRemovesDots() {
        return this.defaultMTerminatorRemovesDots;
    }

    public double getWordBoundaryMinLength() {
        return this.wordBoundaryMinLength;
    }

    public boolean getAddSpaceAtWordBoundary() {
        return this.addSpaceAtWordBoundary;
    }

    protected int getMTerminatorLength(TemplateMarkupOutputModel<?> mTerminator) throws TemplateModelException {
        MarkupOutputFormat format = mTerminator.getOutputFormat();
        if (isHTMLOrXML(format)) {
            return getLengthWithoutTags(format.getMarkupString(mTerminator));
        }
        return 3;
    }

    protected boolean getTerminatorRemovesDots(String terminator) throws TemplateModelException {
        return terminator.startsWith(".") || terminator.startsWith("…");
    }

    protected boolean getMTerminatorRemovesDots(TemplateMarkupOutputModel terminator) throws TemplateModelException {
        if (isHTMLOrXML(terminator.getOutputFormat())) {
            return doesHtmlOrXmlStartWithDot(terminator.getOutputFormat().getMarkupString(terminator));
        }
        return true;
    }

    private TemplateModel unifiedTruncate(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, TruncationMode mode, boolean allowMarkupResult) throws TemplateException {
        Boolean terminatorRemovesDots;
        if (s.length() <= maxLength) {
            return new SimpleScalar(s);
        }
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength can't be negative");
        }
        if (terminator == null) {
            if (allowMarkupResult && this.defaultMTerminator != null) {
                terminator = this.defaultMTerminator;
                terminatorLength = this.defaultMTerminatorLength;
                terminatorRemovesDots = Boolean.valueOf(this.defaultMTerminatorRemovesDots);
            } else {
                terminator = this.defaultTerminator;
                terminatorLength = Integer.valueOf(this.defaultTerminatorLength);
                terminatorRemovesDots = Boolean.valueOf(this.defaultTerminatorRemovesDots);
            }
        } else {
            if (terminatorLength != null) {
                if (terminatorLength.intValue() < 0) {
                    throw new IllegalArgumentException("terminatorLength can't be negative");
                }
            } else {
                terminatorLength = Integer.valueOf(getTerminatorLength(terminator));
            }
            terminatorRemovesDots = null;
        }
        StringBuilder truncatedS = unifiedTruncateWithoutTerminatorAdded(s, maxLength, terminator, terminatorLength.intValue(), terminatorRemovesDots, mode);
        if (truncatedS == null || truncatedS.length() == 0) {
            return terminator;
        }
        if (terminator instanceof TemplateScalarModel) {
            truncatedS.append(((TemplateScalarModel) terminator).getAsString());
            return new SimpleScalar(truncatedS.toString());
        }
        if (terminator instanceof TemplateMarkupOutputModel) {
            TemplateMarkupOutputModel markup = (TemplateMarkupOutputModel) terminator;
            MarkupOutputFormat outputFormat = markup.getOutputFormat();
            return outputFormat.concat(outputFormat.fromPlainTextByEscaping(truncatedS.toString()), markup);
        }
        throw new IllegalArgumentException("Unsupported terminator type: " + ClassUtil.getFTLTypeDescription(terminator));
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x010d A[PHI: r11 r19
      0x010d: PHI (r11v7 'terminatorRemovesDots' java.lang.Boolean) = 
      (r11v0 'terminatorRemovesDots' java.lang.Boolean)
      (r11v0 'terminatorRemovesDots' java.lang.Boolean)
      (r11v8 'terminatorRemovesDots' java.lang.Boolean)
      (r11v8 'terminatorRemovesDots' java.lang.Boolean)
     binds: [B:38:0x00c8, B:40:0x00d0, B:45:0x00e7, B:52:0x0107] A[DONT_GENERATE, DONT_INLINE]
      0x010d: PHI (r19v3 'wbLastCIdx' int) = (r19v1 'wbLastCIdx' int), (r19v1 'wbLastCIdx' int), (r19v1 'wbLastCIdx' int), (r19v4 'wbLastCIdx' int) binds: [B:38:0x00c8, B:40:0x00d0, B:45:0x00e7, B:52:0x0107] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.StringBuilder unifiedTruncateWithoutTerminatorAdded(java.lang.String r7, int r8, freemarker.template.TemplateModel r9, int r10, java.lang.Boolean r11, freemarker.core.DefaultTruncateBuiltinAlgorithm.TruncationMode r12) throws freemarker.template.TemplateModelException {
        /*
            Method dump skipped, instructions count: 558
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.DefaultTruncateBuiltinAlgorithm.unifiedTruncateWithoutTerminatorAdded(java.lang.String, int, freemarker.template.TemplateModel, int, java.lang.Boolean, freemarker.core.DefaultTruncateBuiltinAlgorithm$TruncationMode):java.lang.StringBuilder");
    }

    private int getTerminatorLength(TemplateModel terminator) throws TemplateModelException {
        if (terminator instanceof TemplateScalarModel) {
            return ((TemplateScalarModel) terminator).getAsString().length();
        }
        return getMTerminatorLength((TemplateMarkupOutputModel) terminator);
    }

    private boolean getTerminatorRemovesDots(TemplateModel terminator) throws TemplateModelException {
        if (terminator instanceof TemplateScalarModel) {
            return getTerminatorRemovesDots(((TemplateScalarModel) terminator).getAsString());
        }
        return getMTerminatorRemovesDots((TemplateMarkupOutputModel) terminator);
    }

    private int skipTrailingWS(String s, int lastCIdx) {
        while (lastCIdx >= 0 && Character.isWhitespace(s.charAt(lastCIdx))) {
            lastCIdx--;
        }
        return lastCIdx;
    }

    private int skipTrailingDots(String s, int lastCIdx) {
        while (lastCIdx >= 0 && isDot(s.charAt(lastCIdx))) {
            lastCIdx--;
        }
        return lastCIdx;
    }

    private boolean isWordEnd(String s, int lastCIdx) {
        return lastCIdx + 1 >= s.length() || Character.isWhitespace(s.charAt(lastCIdx + 1));
    }

    private static boolean isDot(char c) {
        return c == '.' || c == 8230;
    }

    private static boolean isDotOrWS(char c) {
        return isDot(c) || Character.isWhitespace(c);
    }

    private boolean isHTMLOrXML(MarkupOutputFormat<?> outputFormat) {
        return (outputFormat instanceof HTMLOutputFormat) || (outputFormat instanceof XMLOutputFormat);
    }

    static int getLengthWithoutTags(String s) {
        int result = 0;
        int i = 0;
        int len = s.length();
        while (i < len) {
            int i2 = i;
            i++;
            char c = s.charAt(i2);
            if (c == '<') {
                if (s.startsWith("!--", i)) {
                    int i3 = i + 3;
                    while (i3 + 2 < len && (s.charAt(i3) != '-' || s.charAt(i3 + 1) != '-' || s.charAt(i3 + 2) != '>')) {
                        i3++;
                    }
                    i = i3 + 3;
                    if (i >= len) {
                        break;
                    }
                } else if (s.startsWith("![CDATA[", i)) {
                    int i4 = i + 8;
                    while (i4 < len && (s.charAt(i4) != ']' || i4 + 2 >= len || s.charAt(i4 + 1) != ']' || s.charAt(i4 + 2) != '>')) {
                        result++;
                        i4++;
                    }
                    i = i4 + 3;
                    if (i >= len) {
                        break;
                    }
                } else {
                    while (i < len && s.charAt(i) != '>') {
                        i++;
                    }
                    i++;
                    if (i >= len) {
                        break;
                    }
                }
            } else if (c == '&') {
                while (i < len && s.charAt(i) != ';') {
                    i++;
                }
                i++;
                result++;
                if (i >= len) {
                    break;
                }
            } else {
                result++;
            }
        }
        return result;
    }

    static boolean doesHtmlOrXmlStartWithDot(String s) {
        char c;
        int i = 0;
        int len = s.length();
        while (i < len) {
            int i2 = i;
            int i3 = i + 1;
            char c2 = s.charAt(i2);
            if (c2 == '<') {
                if (s.startsWith("!--", i3)) {
                    int i4 = i3 + 3;
                    while (i4 + 2 < len && (s.charAt(i4) != '-' || s.charAt(i4 + 1) != '-' || s.charAt(i4 + 2) != '>')) {
                        i4++;
                    }
                    i = i4 + 3;
                    if (i >= len) {
                        return false;
                    }
                } else if (s.startsWith("![CDATA[", i3)) {
                    int i5 = i3 + 8;
                    if (i5 < len && ((c = s.charAt(i5)) != ']' || i5 + 2 >= len || s.charAt(i5 + 1) != ']' || s.charAt(i5 + 2) != '>')) {
                        return isDot(c);
                    }
                    i = i5 + 3;
                    if (i >= len) {
                        return false;
                    }
                } else {
                    while (i3 < len && s.charAt(i3) != '>') {
                        i3++;
                    }
                    i = i3 + 1;
                    if (i >= len) {
                        return false;
                    }
                }
            } else {
                if (c2 == '&') {
                    while (i3 < len && s.charAt(i3) != ';') {
                        i3++;
                    }
                    return isDotCharReference(s.substring(i3, i3));
                }
                return isDot(c2);
            }
        }
        return false;
    }

    static boolean isDotCharReference(String name) {
        if (name.length() <= 2 || name.charAt(0) != '#') {
            return name.equals("hellip") || name.equals("period");
        }
        int charCode = getCodeFromNumericalCharReferenceName(name);
        return charCode == 8230 || charCode == 46;
    }

    static int getCodeFromNumericalCharReferenceName(String name) {
        int i;
        int i2;
        char c = name.charAt(1);
        boolean hex = c == 'x' || c == 'X';
        int code = 0;
        for (int pos = hex ? 2 : 1; pos < name.length(); pos++) {
            char c2 = name.charAt(pos);
            int code2 = code * (hex ? 16 : 10);
            if (c2 >= '0' && c2 <= '9') {
                i = code2;
                i2 = c2 - '0';
            } else if (hex && c2 >= 'a' && c2 <= 'f') {
                i = code2;
                i2 = (c2 - 'a') + 10;
            } else if (hex && c2 >= 'A' && c2 <= 'F') {
                i = code2;
                i2 = (c2 - 'A') + 10;
            } else {
                return -1;
            }
            code = i + i2;
        }
        return code;
    }
}
