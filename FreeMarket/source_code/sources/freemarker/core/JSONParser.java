package freemarker.core;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.Constants;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JSONParser.class */
class JSONParser {
    private static final String UNCLOSED_OBJECT_MESSAGE = "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")";
    private static final String UNCLOSED_ARRAY_MESSAGE = "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")";
    private final String src;
    private final int ln;
    private int p;
    private static final BigDecimal MIN_INT_AS_BIGDECIMAL = BigDecimal.valueOf(-2147483648L);
    private static final BigDecimal MAX_INT_AS_BIGDECIMAL = BigDecimal.valueOf(2147483647L);
    private static final BigDecimal MIN_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);
    private static final BigDecimal MAX_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
    private static int MAX_QUOTATION_LENGTH = 50;

    public static TemplateModel parse(String src) throws JSONParseException {
        return new JSONParser(src).parse();
    }

    private JSONParser(String src) {
        this.src = src;
        this.ln = src.length();
    }

    private TemplateModel parse() throws JSONParseException {
        skipWS();
        TemplateModel result = consumeValue("Empty JSON (contains no value)", this.p);
        skipWS();
        if (this.p != this.ln) {
            throw newParseException("End-of-file was expected but found further non-whitespace characters.");
        }
        return result;
    }

    private TemplateModel consumeValue(String eofErrorMessage, int eofBlamePosition) throws JSONParseException {
        if (this.p == this.ln) {
            throw newParseException(eofErrorMessage == null ? "A value was expected here, but end-of-file was reached." : eofErrorMessage, eofBlamePosition == -1 ? this.p : eofBlamePosition);
        }
        TemplateModel result = tryConsumeString();
        if (result != null) {
            return result;
        }
        TemplateModel result2 = tryConsumeNumber();
        if (result2 != null) {
            return result2;
        }
        TemplateModel result3 = tryConsumeObject();
        if (result3 != null) {
            return result3;
        }
        TemplateModel result4 = tryConsumeArray();
        if (result4 != null) {
            return result4;
        }
        TemplateModel result5 = tryConsumeTrueFalseNull();
        if (result5 != null) {
            if (result5 != TemplateNullModel.INSTANCE) {
                return result5;
            }
            return null;
        }
        if (this.p < this.ln && this.src.charAt(this.p) == '\'') {
            throw newParseException("Unexpected apostrophe-quote character. JSON strings must be quoted with quotation mark.");
        }
        throw newParseException("Expected either the beginning of a (negative) number or the beginning of one of these: {...}, [...], \"...\", true, false, null. Found character " + StringUtil.jQuote(Character.valueOf(this.src.charAt(this.p))) + " instead.");
    }

    private TemplateModel tryConsumeTrueFalseNull() throws JSONParseException {
        int startP = this.p;
        if (this.p < this.ln && isIdentifierStart(this.src.charAt(this.p))) {
            this.p++;
            while (this.p < this.ln && isIdentifierPart(this.src.charAt(this.p))) {
                this.p++;
            }
        }
        if (startP == this.p) {
            return null;
        }
        String keyword = this.src.substring(startP, this.p);
        if (keyword.equals("true")) {
            return TemplateBooleanModel.TRUE;
        }
        if (keyword.equals("false")) {
            return TemplateBooleanModel.FALSE;
        }
        if (keyword.equals(BeanDefinitionParserDelegate.NULL_ELEMENT)) {
            return TemplateNullModel.INSTANCE;
        }
        throw newParseException("Invalid JSON keyword: " + StringUtil.jQuote(keyword) + ". Should be one of: true, false, null. If it meant to be a string then it must be quoted.", startP);
    }

    private TemplateNumberModel tryConsumeNumber() throws JSONParseException {
        char c;
        Number numberValueOf;
        if (this.p >= this.ln) {
            return null;
        }
        char c2 = this.src.charAt(this.p);
        boolean negative = c2 == '-';
        if (!negative && !isDigit(c2) && c2 != '.') {
            return null;
        }
        int startP = this.p;
        if (negative) {
            if (this.p + 1 >= this.ln) {
                throw newParseException("Expected a digit after \"-\", but reached end-of-file.");
            }
            char lookAheadC = this.src.charAt(this.p + 1);
            if (!isDigit(lookAheadC) && lookAheadC != '.') {
                return null;
            }
            this.p++;
        }
        long longSum = 0;
        boolean firstDigit = true;
        while (true) {
            c = this.src.charAt(this.p);
            if (!isDigit(c)) {
                if (c == '.' && firstDigit) {
                    throw newParseException("JSON doesn't allow numbers starting with \".\".");
                }
            } else {
                int digit = c - '0';
                if (longSum == 0) {
                    if (!firstDigit) {
                        throw newParseException("JSON doesn't allow superfluous leading 0-s.", this.p - 1);
                    }
                    longSum = !negative ? digit : -digit;
                    this.p++;
                } else {
                    long prevLongSum = longSum;
                    longSum = (longSum * 10) + (!negative ? digit : -digit);
                    if ((!negative && prevLongSum > longSum) || (negative && prevLongSum < longSum)) {
                        break;
                    }
                    this.p++;
                }
                firstDigit = false;
                if (this.p >= this.ln) {
                    break;
                }
            }
        }
        if (this.p < this.ln && isBigDecimalFittingTailCharacter(c)) {
            char lastC = c;
            this.p++;
            while (this.p < this.ln) {
                char c3 = this.src.charAt(this.p);
                if (isBigDecimalFittingTailCharacter(c3)) {
                    this.p++;
                } else {
                    if ((c3 != '+' && c3 != '-') || !isE(lastC)) {
                        break;
                    }
                    this.p++;
                }
                lastC = c3;
            }
            String numStr = this.src.substring(startP, this.p);
            try {
                BigDecimal bd = new BigDecimal(numStr);
                if (bd.compareTo(MIN_INT_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_INT_AS_BIGDECIMAL) <= 0) {
                    if (NumberUtil.isIntegerBigDecimal(bd)) {
                        return new SimpleNumber(bd.intValue());
                    }
                } else if (bd.compareTo(MIN_LONG_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_LONG_AS_BIGDECIMAL) <= 0 && NumberUtil.isIntegerBigDecimal(bd)) {
                    return new SimpleNumber(bd.longValue());
                }
                return new SimpleNumber(bd);
            } catch (NumberFormatException e) {
                throw new JSONParseException("Malformed number: " + numStr, this.src, startP, e);
            }
        }
        if (longSum <= 2147483647L && longSum >= -2147483648L) {
            numberValueOf = Integer.valueOf((int) longSum);
        } else {
            numberValueOf = Long.valueOf(longSum);
        }
        return new SimpleNumber(numberValueOf);
    }

    private TemplateScalarModel tryConsumeString() throws JSONParseException {
        int startP = this.p;
        if (!tryConsumeChar('\"')) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (this.p < this.ln) {
            char c = this.src.charAt(this.p);
            if (c == '\"') {
                this.p++;
                return new SimpleScalar(sb.toString());
            }
            if (c == '\\') {
                this.p++;
                sb.append(consumeAfterBackslash());
            } else {
                if (c <= 31) {
                    throw newParseException("JSON doesn't allow unescaped control characters in string literals, but found character with code (decimal): " + ((int) c));
                }
                this.p++;
                sb.append(c);
            }
        }
        throw newParseException("String literal was still unclosed when the end of the file was reached. (Look for missing or accidentally escaped closing quotation mark.)", startP);
    }

    private TemplateSequenceModel tryConsumeArray() throws JSONParseException {
        int startP = this.p;
        if (!tryConsumeChar('[')) {
            return null;
        }
        skipWS();
        if (tryConsumeChar(']')) {
            return Constants.EMPTY_SEQUENCE;
        }
        boolean afterComma = false;
        SimpleSequence elements = new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        do {
            skipWS();
            elements.add(consumeValue(afterComma ? null : UNCLOSED_ARRAY_MESSAGE, afterComma ? -1 : startP));
            skipWS();
            afterComma = true;
        } while (consumeChar(',', ']', UNCLOSED_ARRAY_MESSAGE, startP) == ',');
        return elements;
    }

    private TemplateHashModelEx2 tryConsumeObject() throws JSONParseException {
        int startP = this.p;
        if (!tryConsumeChar('{')) {
            return null;
        }
        skipWS();
        if (tryConsumeChar('}')) {
            return Constants.EMPTY_HASH_EX2;
        }
        boolean afterComma = false;
        Map<String, Object> map = new LinkedHashMap<>();
        do {
            skipWS();
            int keyStartP = this.p;
            Object key = consumeValue(afterComma ? null : UNCLOSED_OBJECT_MESSAGE, afterComma ? -1 : startP);
            if (!(key instanceof TemplateScalarModel)) {
                throw newParseException("Wrong key type. JSON only allows string keys inside {...}.", keyStartP);
            }
            try {
                String strKey = ((TemplateScalarModel) key).getAsString();
                skipWS();
                consumeChar(':');
                skipWS();
                map.put(strKey, consumeValue(null, -1));
                skipWS();
                afterComma = true;
            } catch (TemplateModelException e) {
                throw new BugException(e);
            }
        } while (consumeChar(',', '}', UNCLOSED_OBJECT_MESSAGE, startP) == ',');
        return new SimpleHash(map, _ObjectWrappers.SAFE_OBJECT_WRAPPER, 0);
    }

    private boolean isE(char c) {
        return c == 'e' || c == 'E';
    }

    private boolean isBigDecimalFittingTailCharacter(char c) {
        return c == '.' || isE(c) || isDigit(c);
    }

    private char consumeAfterBackslash() throws JSONParseException {
        if (this.p == this.ln) {
            throw newParseException("Reached the end of the file, but the escape is unclosed.");
        }
        char c = this.src.charAt(this.p);
        switch (c) {
            case '\"':
            case '/':
            case '\\':
                this.p++;
                return c;
            case 'b':
                this.p++;
                return '\b';
            case 'f':
                this.p++;
                return '\f';
            case 'n':
                this.p++;
                return '\n';
            case 'r':
                this.p++;
                return '\r';
            case 't':
                this.p++;
                return '\t';
            case 'u':
                this.p++;
                return consumeAfterBackslashU();
            default:
                throw newParseException("Unsupported escape: \\" + c);
        }
    }

    private char consumeAfterBackslashU() throws JSONParseException {
        if (this.p + 3 >= this.ln) {
            throw newParseException("\\u must be followed by exactly 4 hexadecimal digits");
        }
        String hex = this.src.substring(this.p, this.p + 4);
        try {
            char r = (char) Integer.parseInt(hex, 16);
            this.p += 4;
            return r;
        } catch (NumberFormatException e) {
            throw newParseException("\\u must be followed by exactly 4 hexadecimal digits, but was followed by " + StringUtil.jQuote(hex) + ".");
        }
    }

    private boolean tryConsumeChar(char c) {
        if (this.p < this.ln && this.src.charAt(this.p) == c) {
            this.p++;
            return true;
        }
        return false;
    }

    private void consumeChar(char expected) throws JSONParseException {
        consumeChar(expected, (char) 0, null, -1);
    }

    private char consumeChar(char expected1, char expected2, String eofErrorHint, int eofErrorP) throws JSONParseException {
        String str;
        if (this.p >= this.ln) {
            if (eofErrorHint == null) {
                str = "Expected " + StringUtil.jQuote(Character.valueOf(expected1)) + (expected2 != 0 ? " or " + StringUtil.jQuote(Character.valueOf(expected2)) : "") + " character, but reached end-of-file. ";
            } else {
                str = eofErrorHint;
            }
            throw newParseException(str, eofErrorP == -1 ? this.p : eofErrorP);
        }
        char c = this.src.charAt(this.p);
        if (c == expected1 || (expected2 != 0 && c == expected2)) {
            this.p++;
            return c;
        }
        throw newParseException("Expected " + StringUtil.jQuote(Character.valueOf(expected1)) + (expected2 != 0 ? " or " + StringUtil.jQuote(Character.valueOf(expected2)) : "") + " character, but found " + StringUtil.jQuote(Character.valueOf(c)) + " instead.");
    }

    private void skipWS() throws JSONParseException {
        while (true) {
            if (this.p < this.ln && isWS(this.src.charAt(this.p))) {
                this.p++;
            } else if (!skipComment()) {
                return;
            }
        }
    }

    private boolean skipComment() throws JSONParseException {
        if (this.p + 1 < this.ln && this.src.charAt(this.p) == '/') {
            char c2 = this.src.charAt(this.p + 1);
            if (c2 == '/') {
                int eolP = this.p + 2;
                while (eolP < this.ln && !isLineBreak(this.src.charAt(eolP))) {
                    eolP++;
                }
                this.p = eolP;
                return true;
            }
            if (c2 == '*') {
                int closerP = this.p + 3;
                while (closerP < this.ln && (this.src.charAt(closerP - 1) != '*' || this.src.charAt(closerP) != '/')) {
                    closerP++;
                }
                if (closerP >= this.ln) {
                    throw newParseException("Unclosed comment");
                }
                this.p = closerP + 1;
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isWS(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == 160 || c == 65279;
    }

    private static boolean isLineBreak(char c) {
        return c == '\r' || c == '\n';
    }

    private static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || isDigit(c);
    }

    private JSONParseException newParseException(String message) {
        return newParseException(message, this.p);
    }

    private JSONParseException newParseException(String message, int p) {
        return new JSONParseException(message, this.src, p);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/JSONParser$JSONParseException.class */
    static class JSONParseException extends Exception {
        public JSONParseException(String message, String src, int position) {
            super(JSONParser.createSourceCodeErrorMessage(message, src, position));
        }

        public JSONParseException(String message, String src, int position, Throwable cause) {
            super(JSONParser.createSourceCodeErrorMessage(message, src, position), cause);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c0  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00fc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String createSourceCodeErrorMessage(java.lang.String r6, java.lang.String r7, int r8) {
        /*
            Method dump skipped, instructions count: 539
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core.JSONParser.createSourceCodeErrorMessage(java.lang.String, java.lang.String, int):java.lang.String");
    }

    private static String expandTabs(String s, int tabWidth) {
        return expandTabs(s, tabWidth, 0);
    }

    private static String expandTabs(String s, int tabWidth, int startCol) {
        int e = s.indexOf(9);
        if (e == -1) {
            return s;
        }
        int b = 0;
        StringBuilder buf = new StringBuilder(s.length() + Math.max(16, tabWidth * 2));
        do {
            buf.append((CharSequence) s, b, e);
            int col = buf.length() + startCol;
            for (int i = (tabWidth * (1 + (col / tabWidth))) - col; i > 0; i--) {
                buf.append(' ');
            }
            b = e + 1;
            e = s.indexOf(9, b);
        } while (e != -1);
        buf.append((CharSequence) s, b, s.length());
        return buf.toString();
    }
}
