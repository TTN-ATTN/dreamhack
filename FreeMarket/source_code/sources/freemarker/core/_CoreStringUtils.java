package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.util.Collection;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_CoreStringUtils.class */
public final class _CoreStringUtils {
    private _CoreStringUtils() {
    }

    public static String toFTLIdentifierReferenceAfterDot(String name) {
        return backslashEscapeIdentifier(name);
    }

    public static String toFTLTopLevelIdentifierReference(String name) {
        return backslashEscapeIdentifier(name);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0027  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String toFTLTopLevelTragetIdentifier(java.lang.String r3) {
        /*
            r0 = 0
            r4 = r0
            r0 = 0
            r5 = r0
        L4:
            r0 = r5
            r1 = r3
            int r1 = r1.length()
            if (r0 >= r1) goto L50
            r0 = r3
            r1 = r5
            char r0 = r0.charAt(r1)
            r6 = r0
            r0 = r5
            if (r0 != 0) goto L20
            r0 = r6
            boolean r0 = freemarker.template.utility.StringUtil.isFTLIdentifierStart(r0)
            if (r0 == 0) goto L27
            goto L4a
        L20:
            r0 = r6
            boolean r0 = freemarker.template.utility.StringUtil.isFTLIdentifierPart(r0)
            if (r0 != 0) goto L4a
        L27:
            r0 = r6
            r1 = 64
            if (r0 == r1) goto L4a
            r0 = r4
            if (r0 == 0) goto L37
            r0 = r4
            r1 = 92
            if (r0 != r1) goto L44
        L37:
            r0 = r6
            boolean r0 = freemarker.template.utility.StringUtil.isBackslashEscapedFTLIdentifierCharacter(r0)
            if (r0 == 0) goto L44
            r0 = 92
            r4 = r0
            goto L4a
        L44:
            r0 = 34
            r4 = r0
            goto L50
        L4a:
            int r5 = r5 + 1
            goto L4
        L50:
            r0 = r4
            switch(r0) {
                case 0: goto L74;
                case 34: goto L76;
                case 92: goto L7b;
                default: goto L80;
            }
        L74:
            r0 = r3
            return r0
        L76:
            r0 = r3
            java.lang.String r0 = freemarker.template.utility.StringUtil.ftlQuote(r0)
            return r0
        L7b:
            r0 = r3
            java.lang.String r0 = backslashEscapeIdentifier(r0)
            return r0
        L80:
            freemarker.core.BugException r0 = new freemarker.core.BugException
            r1 = r0
            r1.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.core._CoreStringUtils.toFTLTopLevelTragetIdentifier(java.lang.String):java.lang.String");
    }

    public static String backslashEscapeIdentifier(String name) {
        StringBuilder sb = null;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (StringUtil.isBackslashEscapedFTLIdentifierCharacter(c)) {
                if (sb == null) {
                    sb = new StringBuilder(name.length() + 8);
                    sb.append((CharSequence) name, 0, i);
                }
                sb.append('\\');
            }
            if (sb != null) {
                sb.append(c);
            }
        }
        return sb == null ? name : sb.toString();
    }

    public static int getIdentifierNamingConvention(String name) {
        int ln = name.length();
        for (int i = 0; i < ln; i++) {
            char c = name.charAt(i);
            if (c == '_') {
                return 11;
            }
            if (isUpperUSASCII(c)) {
                return 12;
            }
        }
        return 10;
    }

    public static String camelCaseToUnderscored(String camelCaseName) {
        int i = 0;
        while (i < camelCaseName.length() && Character.isLowerCase(camelCaseName.charAt(i))) {
            i++;
        }
        if (i == camelCaseName.length()) {
            return camelCaseName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(camelCaseName.substring(0, i));
        while (i < camelCaseName.length()) {
            char c = camelCaseName.charAt(i);
            if (isUpperUSASCII(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    public static boolean isUpperUSASCII(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static String commaSeparatedJQuotedItems(Collection<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(StringUtil.jQuote(item));
        }
        return sb.toString();
    }
}
