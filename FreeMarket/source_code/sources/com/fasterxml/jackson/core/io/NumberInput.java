package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/io/NumberInput.class */
public final class NumberInput {
    public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
    static final long L_BILLION = 1000000000;
    static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

    public static int parseInt(char[] ch2, int off, int len) {
        int num = ch2[(off + len) - 1] - '0';
        switch (len) {
            case 9:
                off++;
                num += (ch2[off] - '0') * 100000000;
            case 8:
                int i = off;
                off++;
                num += (ch2[i] - '0') * 10000000;
            case 7:
                int i2 = off;
                off++;
                num += (ch2[i2] - '0') * 1000000;
            case 6:
                int i3 = off;
                off++;
                num += (ch2[i3] - '0') * 100000;
            case 5:
                int i4 = off;
                off++;
                num += (ch2[i4] - '0') * 10000;
            case 4:
                int i5 = off;
                off++;
                num += (ch2[i5] - '0') * 1000;
            case 3:
                int i6 = off;
                off++;
                num += (ch2[i6] - '0') * 100;
            case 2:
                num += (ch2[off] - '0') * 10;
                break;
        }
        return num;
    }

    public static int parseInt(String s) {
        char c = s.charAt(0);
        int len = s.length();
        boolean neg = c == '-';
        int offset = 1;
        if (neg) {
            if (len != 1 && len <= 10) {
                offset = 1 + 1;
                c = s.charAt(1);
            } else {
                return Integer.parseInt(s);
            }
        } else if (len > 9) {
            return Integer.parseInt(s);
        }
        if (c > '9' || c < '0') {
            return Integer.parseInt(s);
        }
        int num = c - '0';
        if (offset < len) {
            int i = offset;
            int offset2 = offset + 1;
            char c2 = s.charAt(i);
            if (c2 > '9' || c2 < '0') {
                return Integer.parseInt(s);
            }
            num = (num * 10) + (c2 - '0');
            if (offset2 < len) {
                int offset3 = offset2 + 1;
                char c3 = s.charAt(offset2);
                if (c3 > '9' || c3 < '0') {
                    return Integer.parseInt(s);
                }
                num = (num * 10) + (c3 - '0');
                if (offset3 < len) {
                    do {
                        int i2 = offset3;
                        offset3++;
                        char c4 = s.charAt(i2);
                        if (c4 > '9' || c4 < '0') {
                            return Integer.parseInt(s);
                        }
                        num = (num * 10) + (c4 - '0');
                    } while (offset3 < len);
                }
            }
        }
        return neg ? -num : num;
    }

    public static long parseLong(char[] ch2, int off, int len) {
        int len1 = len - 9;
        long val = parseInt(ch2, off, len1) * L_BILLION;
        return val + parseInt(ch2, off + len1, 9);
    }

    public static long parseLong(String s) {
        int length = s.length();
        if (length <= 9) {
            return parseInt(s);
        }
        return Long.parseLong(s);
    }

    public static boolean inLongRange(char[] ch2, int off, int len, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) {
            return true;
        }
        if (len > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = ch2[off + i] - cmpStr.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static boolean inLongRange(String s, boolean negative) {
        String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmp.length();
        int alen = s.length();
        if (alen < cmpLen) {
            return true;
        }
        if (alen > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = s.charAt(i) - cmp.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static int parseAsInt(String s, int def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        char sign = s2.charAt(0);
        if (sign == '+') {
            s2 = s2.substring(1);
            len = s2.length();
        } else if (sign == '-') {
            i = 1;
        }
        while (i < len) {
            char c = s2.charAt(i);
            if (c <= '9' && c >= '0') {
                i++;
            } else {
                try {
                    return (int) parseDouble(s2);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Integer.parseInt(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static long parseAsLong(String s, long def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        char sign = s2.charAt(0);
        if (sign == '+') {
            s2 = s2.substring(1);
            len = s2.length();
        } else if (sign == '-') {
            i = 1;
        }
        while (i < len) {
            char c = s2.charAt(i);
            if (c <= '9' && c >= '0') {
                i++;
            } else {
                try {
                    return (long) parseDouble(s2);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Long.parseLong(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static double parseAsDouble(String s, double def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        try {
            return parseDouble(s2);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static double parseDouble(String s) throws NumberFormatException {
        if (NASTY_SMALL_DOUBLE.equals(s)) {
            return Double.MIN_VALUE;
        }
        return Double.parseDouble(s);
    }

    public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
        return BigDecimalParser.parse(s);
    }

    public static BigDecimal parseBigDecimal(char[] ch2, int off, int len) throws NumberFormatException {
        return BigDecimalParser.parse(ch2, off, len);
    }

    public static BigDecimal parseBigDecimal(char[] ch2) throws NumberFormatException {
        return BigDecimalParser.parse(ch2);
    }
}
