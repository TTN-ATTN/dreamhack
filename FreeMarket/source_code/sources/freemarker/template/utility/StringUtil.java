package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.ext.dom._ExtDomApi;
import freemarker.template.Version;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/StringUtil.class */
public class StringUtil {
    private static final char[] ESCAPES = createEscapes();
    private static final char[] LT = {'&', 'l', 't', ';'};
    private static final char[] GT = {'&', 'g', 't', ';'};
    private static final char[] AMP = {'&', 'a', 'm', 'p', ';'};
    private static final char[] QUOT = {'&', 'q', 'u', 'o', 't', ';'};
    private static final char[] HTML_APOS = {'&', '#', '3', '9', ';'};
    private static final char[] XML_APOS = {'&', 'a', 'p', 'o', 's', ';'};
    private static final int NO_ESC = 0;
    private static final int ESC_HEXA = 1;
    private static final int ESC_BACKSLASH = 3;

    @Deprecated
    public static String HTMLEnc(String s) {
        return XMLEncNA(s);
    }

    public static String XMLEnc(String s) {
        return XMLOrHTMLEnc(s, true, true, XML_APOS);
    }

    public static void XMLEnc(String s, Writer out) throws IOException {
        XMLOrHTMLEnc(s, XML_APOS, out);
    }

    public static String XHTMLEnc(String s) {
        return XMLOrHTMLEnc(s, true, true, HTML_APOS);
    }

    public static void XHTMLEnc(String s, Writer out) throws IOException {
        XMLOrHTMLEnc(s, HTML_APOS, out);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00bf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String XMLOrHTMLEnc(java.lang.String r6, boolean r7, boolean r8, char[] r9) {
        /*
            Method dump skipped, instructions count: 460
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.utility.StringUtil.XMLOrHTMLEnc(java.lang.String, boolean, boolean, char[]):java.lang.String");
    }

    private static boolean maybeCDataEndGT(String s, int i) {
        if (i == 0) {
            return true;
        }
        if (s.charAt(i - 1) != ']') {
            return false;
        }
        return i == 1 || s.charAt(i - 2) == ']';
    }

    private static void XMLOrHTMLEnc(String s, char[] apos, Writer out) throws IOException {
        int writtenEnd = 0;
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '>' || c == '&' || c == '\"' || c == '\'') {
                int flushLn = i - writtenEnd;
                if (flushLn != 0) {
                    out.write(s, writtenEnd, flushLn);
                }
                writtenEnd = i + 1;
                switch (c) {
                    case '\"':
                        out.write(QUOT);
                        break;
                    case '&':
                        out.write(AMP);
                        break;
                    case '<':
                        out.write(LT);
                        break;
                    case '>':
                        out.write(GT);
                        break;
                    default:
                        out.write(apos);
                        break;
                }
            }
        }
        if (writtenEnd < ln) {
            out.write(s, writtenEnd, ln - writtenEnd);
        }
    }

    private static int shortArrayCopy(char[] src, char[] dst, int dstOffset) {
        for (char c : src) {
            int i = dstOffset;
            dstOffset++;
            dst[i] = c;
        }
        return dstOffset;
    }

    public static String XMLEncNA(String s) {
        return XMLOrHTMLEnc(s, true, true, null);
    }

    public static String XMLEncQAttr(String s) {
        return XMLOrHTMLEnc(s, false, true, null);
    }

    public static String XMLEncNQG(String s) {
        return XMLOrHTMLEnc(s, false, false, null);
    }

    public static String RTFEnc(String s) {
        int ln = s.length();
        int firstEscIdx = -1;
        int lastEscIdx = 0;
        int plusOutLn = 0;
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '{' || c == '}' || c == '\\') {
                if (firstEscIdx == -1) {
                    firstEscIdx = i;
                }
                lastEscIdx = i;
                plusOutLn++;
            }
        }
        if (firstEscIdx == -1) {
            return s;
        }
        char[] esced = new char[ln + plusOutLn];
        if (firstEscIdx != 0) {
            s.getChars(0, firstEscIdx, esced, 0);
        }
        int dst = firstEscIdx;
        for (int i2 = firstEscIdx; i2 <= lastEscIdx; i2++) {
            char c2 = s.charAt(i2);
            if (c2 == '{' || c2 == '}' || c2 == '\\') {
                int i3 = dst;
                dst++;
                esced[i3] = '\\';
            }
            int i4 = dst;
            dst++;
            esced[i4] = c2;
        }
        if (lastEscIdx != ln - 1) {
            s.getChars(lastEscIdx + 1, ln, esced, dst);
        }
        return String.valueOf(esced);
    }

    public static void RTFEnc(String s, Writer out) throws IOException {
        int writtenEnd = 0;
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '{' || c == '}' || c == '\\') {
                int flushLn = i - writtenEnd;
                if (flushLn != 0) {
                    out.write(s, writtenEnd, flushLn);
                }
                out.write(92);
                writtenEnd = i;
            }
        }
        if (writtenEnd < ln) {
            out.write(s, writtenEnd, ln - writtenEnd);
        }
    }

    public static String URLEnc(String s, String charset) throws UnsupportedEncodingException {
        return URLEnc(s, charset, false);
    }

    public static String URLPathEnc(String s, String charset) throws UnsupportedEncodingException {
        return URLEnc(s, charset, true);
    }

    private static String URLEnc(String s, String charset, boolean keepSlash) throws UnsupportedEncodingException {
        int i;
        int i2;
        int i3;
        int i4;
        int ln = s.length();
        int i5 = 0;
        while (i5 < ln && safeInURL(s.charAt(i5), keepSlash)) {
            i5++;
        }
        if (i5 == ln) {
            return s;
        }
        StringBuilder b = new StringBuilder(ln + (ln / 3) + 2);
        b.append(s.substring(0, i5));
        int encStart = i5;
        while (true) {
            i5++;
            if (i5 >= ln) {
                break;
            }
            char c = s.charAt(i5);
            if (safeInURL(c, keepSlash)) {
                if (encStart != -1) {
                    byte[] o = s.substring(encStart, i5).getBytes(charset);
                    for (byte bc : o) {
                        b.append('%');
                        int c1 = bc & 15;
                        int c2 = (bc >> 4) & 15;
                        b.append((char) (c2 < 10 ? c2 + 48 : (c2 - 10) + 65));
                        if (c1 < 10) {
                            i3 = c1;
                            i4 = 48;
                        } else {
                            i3 = c1 - 10;
                            i4 = 65;
                        }
                        b.append((char) (i3 + i4));
                    }
                    encStart = -1;
                }
                b.append(c);
            } else if (encStart == -1) {
                encStart = i5;
            }
        }
        if (encStart != -1) {
            byte[] o2 = s.substring(encStart, i5).getBytes(charset);
            for (byte bc2 : o2) {
                b.append('%');
                int c12 = bc2 & 15;
                int c22 = (bc2 >> 4) & 15;
                b.append((char) (c22 < 10 ? c22 + 48 : (c22 - 10) + 65));
                if (c12 < 10) {
                    i = c12;
                    i2 = 48;
                } else {
                    i = c12 - 10;
                    i2 = 65;
                }
                b.append((char) (i + i2));
            }
        }
        return b.toString();
    }

    private static boolean safeInURL(char c, boolean keepSlash) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || ((c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.' || c == '!' || c == '~' || ((c >= '\'' && c <= '*') || (keepSlash && c == '/')));
    }

    private static char[] createEscapes() {
        char[] escapes = new char[93];
        for (int i = 0; i < 32; i++) {
            escapes[i] = 1;
        }
        escapes[92] = '\\';
        escapes[39] = '\'';
        escapes[34] = '\"';
        escapes[60] = 'l';
        escapes[62] = 'g';
        escapes[38] = 'a';
        escapes[8] = 'b';
        escapes[9] = 't';
        escapes[10] = 'n';
        escapes[12] = 'f';
        escapes[13] = 'r';
        return escapes;
    }

    public static String FTLStringLiteralEnc(String s, char quotation) {
        return FTLStringLiteralEnc(s, quotation, false);
    }

    public static String FTLStringLiteralEnc(String s) {
        return FTLStringLiteralEnc(s, (char) 0, false);
    }

    private static String FTLStringLiteralEnc(String s, char quotation, boolean addQuotation) {
        char otherQuotation;
        char escape;
        int ln = s.length();
        if (quotation == 0) {
            otherQuotation = 0;
        } else if (quotation == '\"') {
            otherQuotation = '\'';
        } else if (quotation == '\'') {
            otherQuotation = '\"';
        } else {
            throw new IllegalArgumentException("Unsupported quotation character: " + quotation);
        }
        int escLn = ESCAPES.length;
        StringBuilder buf = null;
        int i = 0;
        while (i < ln) {
            char c = s.charAt(i);
            if (c == '=') {
                escape = (i <= 0 || s.charAt(i - 1) != '[') ? (char) 0 : '=';
            } else if (c < escLn) {
                escape = ESCAPES[c];
            } else if (c == '{' && i > 0 && isInterpolationStart(s.charAt(i - 1))) {
                escape = '{';
            } else {
                escape = 0;
            }
            if (escape == 0 || escape == otherQuotation) {
                if (buf != null) {
                    buf.append(c);
                }
            } else {
                if (buf == null) {
                    buf = new StringBuilder(s.length() + 4 + (addQuotation ? 2 : 0));
                    if (addQuotation) {
                        buf.append(quotation);
                    }
                    buf.append(s.substring(0, i));
                }
                if (escape == 1) {
                    buf.append("\\x00");
                    int c2 = (c >> 4) & 15;
                    char c3 = (char) (c & 15);
                    buf.append((char) (c2 < 10 ? c2 + 48 : (c2 - 10) + 65));
                    buf.append((char) (c3 < '\n' ? c3 + '0' : (c3 - '\n') + 65));
                } else {
                    buf.append('\\');
                    buf.append(escape);
                }
            }
            i++;
        }
        if (buf == null) {
            return addQuotation ? quotation + s + quotation : s;
        }
        if (addQuotation) {
            buf.append(quotation);
        }
        return buf.toString();
    }

    private static boolean isInterpolationStart(char c) {
        return c == '$' || c == '#';
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0210  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x021c A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String FTLStringLiteralDec(java.lang.String r6) throws freemarker.core.ParseException {
        /*
            Method dump skipped, instructions count: 622
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.utility.StringUtil.FTLStringLiteralDec(java.lang.String):java.lang.String");
    }

    public static Locale deduceLocale(String input) {
        Locale locale;
        if (input == null) {
            return null;
        }
        Locale.getDefault();
        if (input.length() > 0 && input.charAt(0) == '\"') {
            input = input.substring(1, input.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(input, ",_ ");
        String lang = "";
        if (st.hasMoreTokens()) {
            lang = st.nextToken();
        }
        String country = st.hasMoreTokens() ? st.nextToken() : "";
        if (!st.hasMoreTokens()) {
            locale = new Locale(lang, country);
        } else {
            locale = new Locale(lang, country, st.nextToken());
        }
        return locale;
    }

    public static String capitalize(String s) {
        StringTokenizer st = new StringTokenizer(s, " \t\r\n", true);
        StringBuilder buf = new StringBuilder(s.length());
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            buf.append(tok.substring(0, 1).toUpperCase());
            buf.append(tok.substring(1).toLowerCase());
        }
        return buf.toString();
    }

    public static boolean getYesNo(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("f") || s.equalsIgnoreCase("false")) {
            return false;
        }
        if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase(CustomBooleanEditor.VALUE_YES) || s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true")) {
            return true;
        }
        throw new IllegalArgumentException("Illegal boolean value: " + s);
    }

    public static String[] split(String s, char c) {
        int ln = s.length();
        int i = 0;
        int cnt = 1;
        while (true) {
            int i2 = s.indexOf(c, i);
            if (i2 == -1) {
                break;
            }
            cnt++;
            i = i2 + 1;
        }
        String[] res = new String[cnt];
        int i3 = 0;
        int i4 = 0;
        while (true) {
            int b = i4;
            if (b <= ln) {
                int e = s.indexOf(c, b);
                if (e == -1) {
                    e = ln;
                }
                int i5 = i3;
                i3++;
                res[i5] = s.substring(b, e);
                i4 = e + 1;
            } else {
                return res;
            }
        }
    }

    public static String[] split(String s, String sep, boolean caseInsensitive) {
        int sepLn = sep.length();
        String convertedS = caseInsensitive ? s.toLowerCase() : s;
        int sLn = s.length();
        if (sepLn == 0) {
            String[] res = new String[sLn];
            for (int i = 0; i < sLn; i++) {
                res[i] = String.valueOf(s.charAt(i));
            }
            return res;
        }
        String splitString = caseInsensitive ? sep.toLowerCase() : sep;
        int next = 0;
        int count = 1;
        while (true) {
            int next2 = convertedS.indexOf(splitString, next);
            if (next2 == -1) {
                break;
            }
            count++;
            next = next2 + sepLn;
        }
        String[] res2 = new String[count];
        int dst = 0;
        int i2 = 0;
        while (true) {
            int next3 = i2;
            if (next3 <= sLn) {
                int end = convertedS.indexOf(splitString, next3);
                if (end == -1) {
                    end = sLn;
                }
                int i3 = dst;
                dst++;
                res2[i3] = s.substring(next3, end);
                i2 = end + sepLn;
            } else {
                return res2;
            }
        }
    }

    public static String replace(String text, String oldSub, String newSub) {
        return replace(text, oldSub, newSub, false, false);
    }

    public static String replace(String text, String oldsub, String newsub, boolean caseInsensitive, boolean firstOnly) {
        int oln = oldsub.length();
        if (oln == 0) {
            int nln = newsub.length();
            if (nln == 0) {
                return text;
            }
            if (firstOnly) {
                return newsub + text;
            }
            int tln = text.length();
            StringBuilder buf = new StringBuilder(tln + ((tln + 1) * nln));
            buf.append(newsub);
            for (int i = 0; i < tln; i++) {
                buf.append(text.charAt(i));
                buf.append(newsub);
            }
            return buf.toString();
        }
        String oldsub2 = caseInsensitive ? oldsub.toLowerCase() : oldsub;
        String input = caseInsensitive ? text.toLowerCase() : text;
        int e = input.indexOf(oldsub2);
        if (e == -1) {
            return text;
        }
        int b = 0;
        StringBuilder buf2 = new StringBuilder(text.length() + (Math.max(newsub.length() - oln, 0) * 3));
        do {
            buf2.append(text.substring(b, e));
            buf2.append(newsub);
            b = e + oln;
            e = input.indexOf(oldsub2, b);
            if (e == -1) {
                break;
            }
        } while (!firstOnly);
        buf2.append(text.substring(b));
        return buf2.toString();
    }

    public static String chomp(String s) {
        if (s.endsWith("\r\n")) {
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("\r") || s.endsWith("\n")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String emptyToNull(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        return s;
    }

    public static String jQuote(Object obj) {
        return jQuote(obj != null ? obj.toString() : null);
    }

    public static String jQuote(String s) {
        if (s == null) {
            return BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        return javaStringEnc(s, true);
    }

    public static String jQuoteNoXSS(Object obj) {
        return jQuoteNoXSS(obj != null ? obj.toString() : null);
    }

    public static String jQuoteNoXSS(String s) {
        if (s == null) {
            return BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        int ln = s.length();
        StringBuilder b = new StringBuilder(ln + 6);
        b.append('\"');
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '\"') {
                b.append("\\\"");
            } else if (c == '\\') {
                b.append("\\\\");
            } else if (c == '<') {
                b.append("\\u003C");
            } else if (c < ' ') {
                if (c == '\n') {
                    b.append("\\n");
                } else if (c == '\r') {
                    b.append("\\r");
                } else if (c == '\f') {
                    b.append("\\f");
                } else if (c == '\b') {
                    b.append("\\b");
                } else if (c == '\t') {
                    b.append("\\t");
                } else {
                    b.append("\\u00");
                    b.append(toHexDigitLowerCase(c / 16));
                    b.append(toHexDigitLowerCase(c & 15));
                }
            } else {
                b.append(c);
            }
        }
        b.append('\"');
        return b.toString();
    }

    public static String ftlQuote(String s) {
        char quotation;
        if (s.indexOf(34) != -1 && s.indexOf(39) == -1) {
            quotation = '\'';
        } else {
            quotation = '\"';
        }
        return FTLStringLiteralEnc(s, quotation, true);
    }

    public static boolean isFTLIdentifierStart(char c) {
        if (c >= 170) {
            return c < 43000 ? c < 11631 ? c < 8488 ? c < 8336 ? c < 216 ? c < 186 ? c == 170 || c == 181 : c == 186 || (c >= 192 && c <= 214) : c < 8305 ? (c >= 216 && c <= 246) || (c >= 248 && c <= 8191) : c == 8305 || c == 8319 : c < 8469 ? c < 8455 ? (c >= 8336 && c <= 8348) || c == 8450 : c == 8455 || (c >= 8458 && c <= 8467) : c < 8484 ? c == 8469 || (c >= 8473 && c <= 8477) : c == 8484 || c == 8486 : c < 11312 ? c < 8517 ? c < 8495 ? c == 8488 || (c >= 8490 && c <= 8493) : (c >= 8495 && c <= 8505) || (c >= 8508 && c <= 8511) : c < 8579 ? (c >= 8517 && c <= 8521) || c == 8526 : (c >= 8579 && c <= 8580) || (c >= 11264 && c <= 11310) : c < 11520 ? c < 11499 ? (c >= 11312 && c <= 11358) || (c >= 11360 && c <= 11492) : (c >= 11499 && c <= 11502) || (c >= 11506 && c <= 11507) : c < 11565 ? (c >= 11520 && c <= 11557) || c == 11559 : c == 11565 || (c >= 11568 && c <= 11623) : c < 12784 ? c < 11728 ? c < 11696 ? c < 11680 ? c == 11631 || (c >= 11648 && c <= 11670) : (c >= 11680 && c <= 11686) || (c >= 11688 && c <= 11694) : c < 11712 ? (c >= 11696 && c <= 11702) || (c >= 11704 && c <= 11710) : (c >= 11712 && c <= 11718) || (c >= 11720 && c <= 11726) : c < 12337 ? c < 11823 ? (c >= 11728 && c <= 11734) || (c >= 11736 && c <= 11742) : c == 11823 || (c >= 12293 && c <= 12294) : c < 12352 ? (c >= 12337 && c <= 12341) || (c >= 12347 && c <= 12348) : (c >= 12352 && c <= 12687) || (c >= 12704 && c <= 12730) : c < 42623 ? c < 42192 ? c < 13312 ? (c >= 12784 && c <= 12799) || (c >= 13056 && c <= 13183) : (c >= 13312 && c <= 19893) || (c >= 19968 && c <= 42124) : c < 42512 ? (c >= 42192 && c <= 42237) || (c >= 42240 && c <= 42508) : (c >= 42512 && c <= 42539) || (c >= 42560 && c <= 42606) : c < 42891 ? c < 42775 ? (c >= 42623 && c <= 42647) || (c >= 42656 && c <= 42725) : (c >= 42775 && c <= 42783) || (c >= 42786 && c <= 42888) : c < 42912 ? (c >= 42891 && c <= 42894) || (c >= 42896 && c <= 42899) : c >= 42912 && c <= 42922 : c < 43808 ? c < 43588 ? c < 43259 ? c < 43072 ? c < 43015 ? (c >= 43000 && c <= 43009) || (c >= 43011 && c <= 43013) : (c >= 43015 && c <= 43018) || (c >= 43020 && c <= 43042) : c < 43216 ? (c >= 43072 && c <= 43123) || (c >= 43138 && c <= 43187) : (c >= 43216 && c <= 43225) || (c >= 43250 && c <= 43255) : c < 43396 ? c < 43312 ? c == 43259 || (c >= 43264 && c <= 43301) : (c >= 43312 && c <= 43334) || (c >= 43360 && c <= 43388) : c < 43520 ? (c >= 43396 && c <= 43442) || (c >= 43471 && c <= 43481) : (c >= 43520 && c <= 43560) || (c >= 43584 && c <= 43586) : c < 43712 ? c < 43648 ? c < 43616 ? (c >= 43588 && c <= 43595) || (c >= 43600 && c <= 43609) : (c >= 43616 && c <= 43638) || c == 43642 : c < 43701 ? (c >= 43648 && c <= 43695) || c == 43697 : (c >= 43701 && c <= 43702) || (c >= 43705 && c <= 43709) : c < 43762 ? c < 43739 ? c == 43712 || c == 43714 : (c >= 43739 && c <= 43741) || (c >= 43744 && c <= 43754) : c < 43785 ? (c >= 43762 && c <= 43764) || (c >= 43777 && c <= 43782) : (c >= 43785 && c <= 43790) || (c >= 43793 && c <= 43798) : c < 64326 ? c < 64275 ? c < 44032 ? c < 43968 ? (c >= 43808 && c <= 43814) || (c >= 43816 && c <= 43822) : (c >= 43968 && c <= 44002) || (c >= 44016 && c <= 44025) : c < 55243 ? (c >= 44032 && c <= 55203) || (c >= 55216 && c <= 55238) : (c >= 55243 && c <= 55291) || (c >= 63744 && c <= 64262) : c < 64312 ? c < 64287 ? (c >= 64275 && c <= 64279) || c == 64285 : (c >= 64287 && c <= 64296) || (c >= 64298 && c <= 64310) : c < 64320 ? (c >= 64312 && c <= 64316) || c == 64318 : (c >= 64320 && c <= 64321) || (c >= 64323 && c <= 64324) : c < 65313 ? c < 65008 ? c < 64848 ? (c >= 64326 && c <= 64433) || (c >= 64467 && c <= 64829) : (c >= 64848 && c <= 64911) || (c >= 64914 && c <= 64967) : c < 65142 ? (c >= 65008 && c <= 65019) || (c >= 65136 && c <= 65140) : (c >= 65142 && c <= 65276) || (c >= 65296 && c <= 65305) : c < 65482 ? c < 65382 ? (c >= 65313 && c <= 65338) || (c >= 65345 && c <= 65370) : (c >= 65382 && c <= 65470) || (c >= 65474 && c <= 65479) : c < 65498 ? (c >= 65482 && c <= 65487) || (c >= 65490 && c <= 65495) : c >= 65498 && c <= 65500;
        }
        if (c < 'a' || c > 'z') {
            return (c >= '@' && c <= 'Z') || c == '$' || c == '_';
        }
        return true;
    }

    public static boolean isFTLIdentifierPart(char c) {
        return isFTLIdentifierStart(c) || (c >= '0' && c <= '9');
    }

    public static boolean isBackslashEscapedFTLIdentifierCharacter(char c) {
        return c == '-' || c == '.' || c == ':' || c == '#';
    }

    public static String javaStringEnc(String s) {
        return javaStringEnc(s, false);
    }

    public static String javaStringEnc(String s, boolean quote) {
        int ln = s.length();
        int i = 0;
        while (i < ln) {
            char c = s.charAt(i);
            if (c != '\"' && c != '\\' && c >= ' ') {
                i++;
            } else {
                StringBuilder b = new StringBuilder(ln + (quote ? 6 : 4));
                if (quote) {
                    b.append("\"");
                }
                b.append((CharSequence) s, 0, i);
                while (true) {
                    if (c == '\"') {
                        b.append("\\\"");
                    } else if (c == '\\') {
                        b.append("\\\\");
                    } else if (c < ' ') {
                        if (c == '\n') {
                            b.append("\\n");
                        } else if (c == '\r') {
                            b.append("\\r");
                        } else if (c == '\f') {
                            b.append("\\f");
                        } else if (c == '\b') {
                            b.append("\\b");
                        } else if (c == '\t') {
                            b.append("\\t");
                        } else {
                            b.append("\\u00");
                            b.append(toHexDigitLowerCase(c / 16));
                            b.append(toHexDigitLowerCase(c & 15));
                        }
                    } else {
                        b.append(c);
                    }
                    i++;
                    if (i >= ln) {
                        break;
                    }
                    c = s.charAt(i);
                }
                if (quote) {
                    b.append("\"");
                }
                return b.toString();
            }
        }
        return quote ? '\"' + s + '\"' : s;
    }

    public static String javaScriptStringEnc(String s) {
        return jsStringEnc(s, JsStringEncCompatibility.JAVA_SCRIPT);
    }

    public static String jsonStringEnc(String s) {
        return jsStringEnc(s, JsStringEncCompatibility.JSON);
    }

    @Deprecated
    public static String jsStringEnc(String s, boolean json) {
        return jsStringEnc(s, json ? JsStringEncCompatibility.JSON : JsStringEncCompatibility.JAVA_SCRIPT, null);
    }

    public static String jsStringEnc(String s, JsStringEncCompatibility compatibility) {
        return jsStringEnc(s, compatibility, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:157:0x031e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String jsStringEnc(java.lang.String r5, freemarker.template.utility.StringUtil.JsStringEncCompatibility r6, freemarker.template.utility.StringUtil.JsStringEncQuotation r7) {
        /*
            Method dump skipped, instructions count: 846
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.utility.StringUtil.jsStringEnc(java.lang.String, freemarker.template.utility.StringUtil$JsStringEncCompatibility, freemarker.template.utility.StringUtil$JsStringEncQuotation):java.lang.String");
    }

    private static char toHexDigitLowerCase(int d) {
        return (char) (d < 10 ? d + 48 : (d - 10) + 97);
    }

    private static char toHexDigitUpperCase(int d) {
        return (char) (d < 10 ? d + 48 : (d - 10) + 65);
    }

    /* JADX WARN: Code restructure failed: missing block: B:46:0x0142, code lost:
    
        throw new java.text.ParseException("Expecting \":\" here, but found " + jQuote(java.lang.String.valueOf(r8)) + " at position " + r10 + ".", r10);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.Map parseNameValuePairList(java.lang.String r5, java.lang.String r6) throws java.text.ParseException {
        /*
            Method dump skipped, instructions count: 667
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.template.utility.StringUtil.parseNameValuePairList(java.lang.String, java.lang.String):java.util.Map");
    }

    @Deprecated
    public static boolean isXMLID(String name) {
        return _ExtDomApi.isXMLNameLike(name);
    }

    public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
        return _ExtDomApi.matchesName(qname, nodeName, nsURI, env);
    }

    public static String leftPad(String s, int minLength) {
        return leftPad(s, minLength, ' ');
    }

    public static String leftPad(String s, int minLength, char filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        int dif = minLength - ln;
        for (int i = 0; i < dif; i++) {
            res.append(filling);
        }
        res.append(s);
        return res.toString();
    }

    public static String leftPad(String s, int minLength, String filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        int dif = minLength - ln;
        int fln = filling.length();
        if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
        }
        int cnt = dif / fln;
        for (int i = 0; i < cnt; i++) {
            res.append(filling);
        }
        int cnt2 = dif % fln;
        for (int i2 = 0; i2 < cnt2; i2++) {
            res.append(filling.charAt(i2));
        }
        res.append(s);
        return res.toString();
    }

    public static String rightPad(String s, int minLength) {
        return rightPad(s, minLength, ' ');
    }

    public static String rightPad(String s, int minLength, char filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        res.append(s);
        int dif = minLength - ln;
        for (int i = 0; i < dif; i++) {
            res.append(filling);
        }
        return res.toString();
    }

    public static String rightPad(String s, int minLength, String filling) {
        int ln = s.length();
        if (minLength <= ln) {
            return s;
        }
        StringBuilder res = new StringBuilder(minLength);
        res.append(s);
        int dif = minLength - ln;
        int fln = filling.length();
        if (fln == 0) {
            throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
        }
        int start = ln % fln;
        int end = fln - start <= dif ? fln : start + dif;
        for (int i = start; i < end; i++) {
            res.append(filling.charAt(i));
        }
        int dif2 = dif - (end - start);
        int cnt = dif2 / fln;
        for (int i2 = 0; i2 < cnt; i2++) {
            res.append(filling);
        }
        int cnt2 = dif2 % fln;
        for (int i3 = 0; i3 < cnt2; i3++) {
            res.append(filling.charAt(i3));
        }
        return res.toString();
    }

    public static int versionStringToInt(String version) {
        return new Version(version).intValue();
    }

    public static String tryToString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object.toString();
        } catch (Throwable e) {
            return failedToStringSubstitute(object, e);
        }
    }

    private static String failedToStringSubstitute(Object object, Throwable e) {
        String eStr;
        try {
            eStr = e.toString();
        } catch (Throwable th) {
            eStr = ClassUtil.getShortClassNameOfObject(e);
        }
        return PropertyAccessor.PROPERTY_KEY_PREFIX + ClassUtil.getShortClassNameOfObject(object) + ".toString() failed: " + eStr + "]";
    }

    public static String toUpperABC(int n) {
        return toABC(n, 'A');
    }

    public static String toLowerABC(int n) {
        return toABC(n, 'a');
    }

    private static String toABC(int n, char oneDigit) {
        if (n < 1) {
            throw new IllegalArgumentException("Can't convert 0 or negative numbers to latin-number: " + n);
        }
        int reached = 1;
        int weight = 1;
        while (true) {
            int nextWeight = weight * 26;
            int nextReached = reached + nextWeight;
            if (nextReached > n) {
                break;
            }
            weight = nextWeight;
            reached = nextReached;
        }
        StringBuilder sb = new StringBuilder();
        while (weight != 0) {
            int digitIncrease = (n - reached) / weight;
            sb.append((char) (oneDigit + digitIncrease));
            reached += digitIncrease * weight;
            weight /= 26;
        }
        return sb.toString();
    }

    public static char[] trim(char[] cs) {
        if (cs.length == 0) {
            return cs;
        }
        int start = 0;
        int end = cs.length;
        while (start < end && cs[start] <= ' ') {
            start++;
        }
        while (start < end && cs[end - 1] <= ' ') {
            end--;
        }
        if (start == 0 && end == cs.length) {
            return cs;
        }
        if (start == end) {
            return CollectionUtils.EMPTY_CHAR_ARRAY;
        }
        char[] newCs = new char[end - start];
        System.arraycopy(cs, start, newCs, 0, end - start);
        return newCs;
    }

    public static boolean isTrimmableToEmpty(char[] text) {
        return isTrimmableToEmpty(text, 0, text.length);
    }

    public static boolean isTrimmableToEmpty(char[] text, int start) {
        return isTrimmableToEmpty(text, start, text.length);
    }

    public static boolean isTrimmableToEmpty(char[] text, int start, int end) {
        for (int i = start; i < end; i++) {
            if (text[i] > ' ') {
                return false;
            }
        }
        return true;
    }

    public static Pattern globToRegularExpression(String glob) {
        return globToRegularExpression(glob, false);
    }

    public static Pattern globToRegularExpression(String glob, boolean caseInsensitive) {
        StringBuilder regex = new StringBuilder();
        int nextStart = 0;
        boolean escaped = false;
        int ln = glob.length();
        int idx = 0;
        while (idx < ln) {
            char c = glob.charAt(idx);
            if (!escaped) {
                if (c == '?') {
                    appendLiteralGlobSection(regex, glob, nextStart, idx);
                    regex.append("[^/]");
                    nextStart = idx + 1;
                } else if (c == '*') {
                    appendLiteralGlobSection(regex, glob, nextStart, idx);
                    if (idx + 1 < ln && glob.charAt(idx + 1) == '*') {
                        if (idx != 0 && glob.charAt(idx - 1) != '/') {
                            throw new IllegalArgumentException("The \"**\" wildcard must be directly after a \"/\" or it must be at the beginning, in this glob: " + glob);
                        }
                        if (idx + 2 == ln) {
                            regex.append(".*");
                            idx++;
                        } else {
                            if (idx + 2 >= ln || glob.charAt(idx + 2) != '/') {
                                throw new IllegalArgumentException("The \"**\" wildcard must be followed by \"/\", or must be at tehe end, in this glob: " + glob);
                            }
                            regex.append("(.*?/)*");
                            idx += 2;
                        }
                    } else {
                        regex.append("[^/]*");
                    }
                    nextStart = idx + 1;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '[' || c == '{') {
                    throw new IllegalArgumentException("The \"" + c + "\" glob operator is currently unsupported (precede it with \\ for literal matching), in this glob: " + glob);
                }
            } else {
                escaped = false;
            }
            idx++;
        }
        appendLiteralGlobSection(regex, glob, nextStart, glob.length());
        return Pattern.compile(regex.toString(), caseInsensitive ? 66 : 0);
    }

    private static void appendLiteralGlobSection(StringBuilder regex, String glob, int start, int end) {
        if (start == end) {
            return;
        }
        String part = unescapeLiteralGlobSection(glob.substring(start, end));
        regex.append(Pattern.quote(part));
    }

    private static String unescapeLiteralGlobSection(String s) {
        int iIndexOf;
        int backslashIdx = s.indexOf(92);
        if (backslashIdx == -1) {
            return s;
        }
        int ln = s.length();
        StringBuilder sb = new StringBuilder(ln - 1);
        int nextStart = 0;
        do {
            sb.append((CharSequence) s, nextStart, backslashIdx);
            nextStart = backslashIdx + 1;
            iIndexOf = s.indexOf(92, nextStart + 1);
            backslashIdx = iIndexOf;
        } while (iIndexOf != -1);
        if (nextStart < ln) {
            sb.append((CharSequence) s, nextStart, ln);
        }
        return sb.toString();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/StringUtil$JsStringEncCompatibility.class */
    public enum JsStringEncCompatibility {
        JAVA_SCRIPT(true, false),
        JSON(false, true),
        JAVA_SCRIPT_OR_JSON(true, true);

        private final boolean javaScriptCompatible;
        private final boolean jsonCompatible;

        JsStringEncCompatibility(boolean javaScriptCompatible, boolean jsonCompatible) {
            this.javaScriptCompatible = javaScriptCompatible;
            this.jsonCompatible = jsonCompatible;
        }

        boolean isJSONCompatible() {
            return this.jsonCompatible;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/StringUtil$JsStringEncQuotation.class */
    public enum JsStringEncQuotation {
        QUOTATION_MARK('\"'),
        APOSTROPHE('\'');

        private final char symbol;

        JsStringEncQuotation(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return this.symbol;
        }
    }
}
