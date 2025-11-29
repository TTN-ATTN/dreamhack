package org.apache.tomcat.util.file;

import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/file/Matcher.class */
public final class Matcher {
    public static boolean matchName(Set<String> patternSet, String fileName) {
        char[] fileNameArray = fileName.toCharArray();
        for (String pattern : patternSet) {
            if (match(pattern, fileNameArray, true)) {
                return true;
            }
        }
        return false;
    }

    public static boolean match(String pattern, String str, boolean caseSensitive) {
        return match(pattern, str.toCharArray(), caseSensitive);
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x00c3, code lost:
    
        if (r12 <= r13) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00ce, code lost:
    
        return allStars(r0, r10, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00cf, code lost:
    
        r0 = r0[r11];
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00d9, code lost:
    
        if (r0 == '*') goto L109;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00e0, code lost:
    
        if (r12 <= r13) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00ea, code lost:
    
        if (r0 == '?') goto L112;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00f7, code lost:
    
        if (different(r8, r0, r7[r13]) == false) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00fa, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00fc, code lost:
    
        r11 = r11 - 1;
        r13 = r13 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0109, code lost:
    
        if (r12 <= r13) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0114, code lost:
    
        return allStars(r0, r10, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0119, code lost:
    
        if (r10 == r11) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0120, code lost:
    
        if (r12 > r13) goto L115;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0123, code lost:
    
        r16 = -1;
        r17 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0130, code lost:
    
        if (r17 > r11) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0139, code lost:
    
        if (r0[r17] != '*') goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x013c, code lost:
    
        r16 = r17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x0143, code lost:
    
        r17 = r17 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x014f, code lost:
    
        if (r16 != (r10 + 1)) goto L116;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x0152, code lost:
    
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0158, code lost:
    
        r0 = (r16 - r10) - 1;
        r0 = (r13 - r12) + 1;
        r19 = -1;
        r20 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0177, code lost:
    
        if (r20 > (r0 - r0)) goto L125;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x017a, code lost:
    
        r21 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0181, code lost:
    
        if (r21 >= r0) goto L124;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x0184, code lost:
    
        r0 = r0[(r10 + r21) + 1];
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0193, code lost:
    
        if (r0 == '?') goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01a6, code lost:
    
        if (different(r8, r0, r7[(r12 + r20) + r21]) == false) goto L128;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01ac, code lost:
    
        r21 = r21 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01b2, code lost:
    
        r19 = r12 + r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x01bc, code lost:
    
        r20 = r20 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01c5, code lost:
    
        if (r19 != (-1)) goto L95;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x01c8, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x01ca, code lost:
    
        r10 = r16;
        r12 = r19 + r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x01e0, code lost:
    
        return allStars(r0, r10, r11);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean match(java.lang.String r6, char[] r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 481
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.file.Matcher.match(java.lang.String, char[], boolean):boolean");
    }

    private static boolean allStars(char[] chars, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (chars[i] != '*') {
                return false;
            }
        }
        return true;
    }

    private static boolean different(boolean caseSensitive, char ch2, char other) {
        return caseSensitive ? ch2 != other : Character.toUpperCase(ch2) != Character.toUpperCase(other);
    }
}
