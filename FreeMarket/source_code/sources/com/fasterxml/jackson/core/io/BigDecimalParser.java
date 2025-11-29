package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;
import java.util.Arrays;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/io/BigDecimalParser.class */
public final class BigDecimalParser {
    private static final int MAX_CHARS_TO_REPORT = 1000;
    private final char[] chars;

    BigDecimalParser(char[] chars) {
        this.chars = chars;
    }

    public static BigDecimal parse(String valueStr) {
        return parse(valueStr.toCharArray());
    }

    public static BigDecimal parse(char[] chars, int off, int len) {
        if (off > 0 || len != chars.length) {
            chars = Arrays.copyOfRange(chars, off, off + len);
        }
        return parse(chars);
    }

    public static BigDecimal parse(char[] chars) {
        String stringToReport;
        int len = chars.length;
        try {
            if (len < 500) {
                return new BigDecimal(chars);
            }
            return new BigDecimalParser(chars).parseBigDecimal(len / 10);
        } catch (NumberFormatException e) {
            String desc = e.getMessage();
            if (desc == null) {
                desc = "Not a valid number representation";
            }
            if (chars.length <= 1000) {
                stringToReport = new String(chars);
            } else {
                stringToReport = new String(Arrays.copyOfRange(chars, 0, 1000)) + "(truncated, full length is " + chars.length + " chars)";
            }
            throw new NumberFormatException("Value \"" + stringToReport + "\" can not be represented as `java.math.BigDecimal`, reason: " + desc);
        }
    }

    private BigDecimal parseBigDecimal(int splitLen) throws NumberFormatException {
        int numEndIdx;
        BigDecimal res;
        boolean numHasSign = false;
        boolean expHasSign = false;
        boolean neg = false;
        int numIdx = 0;
        int expIdx = -1;
        int dotIdx = -1;
        int scale = 0;
        int len = this.chars.length;
        for (int i = 0; i < len; i++) {
            char c = this.chars[i];
            switch (c) {
                case '+':
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        break;
                    } else {
                        if (numHasSign) {
                            throw new NumberFormatException("Multiple signs in number");
                        }
                        numHasSign = true;
                        numIdx = i + 1;
                        break;
                    }
                case '-':
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        break;
                    } else {
                        if (numHasSign) {
                            throw new NumberFormatException("Multiple signs in number");
                        }
                        numHasSign = true;
                        neg = true;
                        numIdx = i + 1;
                        break;
                    }
                case '.':
                    if (dotIdx >= 0) {
                        throw new NumberFormatException("Multiple decimal points");
                    }
                    dotIdx = i;
                    break;
                case 'E':
                case 'e':
                    if (expIdx >= 0) {
                        throw new NumberFormatException("Multiple exponent markers");
                    }
                    expIdx = i;
                    break;
                default:
                    if (dotIdx < 0 || expIdx != -1) {
                        break;
                    } else {
                        scale++;
                        break;
                    }
                    break;
            }
        }
        int exp = 0;
        if (expIdx >= 0) {
            numEndIdx = expIdx;
            String expStr = new String(this.chars, expIdx + 1, (len - expIdx) - 1);
            exp = Integer.parseInt(expStr);
            scale = adjustScale(scale, exp);
        } else {
            numEndIdx = len;
        }
        if (dotIdx >= 0) {
            int leftLen = dotIdx - numIdx;
            BigDecimal left = toBigDecimalRec(numIdx, leftLen, exp, splitLen);
            int rightLen = (numEndIdx - dotIdx) - 1;
            BigDecimal right = toBigDecimalRec(dotIdx + 1, rightLen, exp - rightLen, splitLen);
            res = left.add(right);
        } else {
            res = toBigDecimalRec(numIdx, numEndIdx - numIdx, exp, splitLen);
        }
        if (scale != 0) {
            res = res.setScale(scale);
        }
        if (neg) {
            res = res.negate();
        }
        return res;
    }

    private int adjustScale(int scale, long exp) {
        long adjScale = scale - exp;
        if (adjScale > 2147483647L || adjScale < -2147483648L) {
            throw new NumberFormatException("Scale out of range: " + adjScale + " while adjusting scale " + scale + " to exponent " + exp);
        }
        return (int) adjScale;
    }

    private BigDecimal toBigDecimalRec(int off, int len, int scale, int splitLen) {
        if (len <= splitLen) {
            return len == 0 ? BigDecimal.ZERO : new BigDecimal(this.chars, off, len).movePointRight(scale);
        }
        int mid = len / 2;
        BigDecimal left = toBigDecimalRec(off, mid, (scale + len) - mid, splitLen);
        BigDecimal right = toBigDecimalRec(off + mid, len - mid, scale, splitLen);
        return left.add(right);
    }
}
