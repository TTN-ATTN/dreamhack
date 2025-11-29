package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/CTemplateNumberFormat.class */
final class CTemplateNumberFormat extends TemplateNumberFormat {
    private static final float MAX_INCREMENT_1_FLOAT = 1.6777216E7f;
    private static final double MAX_INCREMENT_1_DOUBLE = 9.007199254740992E15d;
    private final String doublePositiveInfinity;
    private final String doubleNegativeInfinity;
    private final String doubleNaN;
    private final String floatPositiveInfinity;
    private final String floatNegativeInfinity;
    private final String floatNaN;

    CTemplateNumberFormat(String doublePositiveInfinity, String doubleNegativeInfinity, String doubleNaN, String floatPositiveInfinity, String floatNegativeInfinity, String floatNaN) {
        this.doublePositiveInfinity = doublePositiveInfinity;
        this.doubleNegativeInfinity = doubleNegativeInfinity;
        this.doubleNaN = doubleNaN;
        this.floatPositiveInfinity = floatPositiveInfinity;
        this.floatNegativeInfinity = floatNegativeInfinity;
        this.floatNaN = floatNaN;
    }

    @Override // freemarker.core.TemplateNumberFormat
    public String formatToPlainText(TemplateNumberModel numberModel) throws TemplateValueFormatException, TemplateModelException {
        Number num = TemplateFormatUtil.getNonNullNumber(numberModel);
        if ((num instanceof Integer) || (num instanceof Long)) {
            return num.toString();
        }
        if (num instanceof Double) {
            double n = num.doubleValue();
            if (n == Double.POSITIVE_INFINITY) {
                return this.doublePositiveInfinity;
            }
            if (n == Double.NEGATIVE_INFINITY) {
                return this.doubleNegativeInfinity;
            }
            if (Double.isNaN(n)) {
                return this.doubleNaN;
            }
            if (Math.floor(n) != n) {
                double absN = Math.abs(n);
                if (absN < 0.001d && absN > 1.0E-7d) {
                    return BigDecimal.valueOf(n).toString();
                }
                if (absN >= 1.0E7d) {
                    return BigDecimal.valueOf(n).toPlainString();
                }
            } else if (Math.abs(n) <= MAX_INCREMENT_1_DOUBLE) {
                return Long.toString((long) n);
            }
            return removeRedundantDot0(Double.toString(n));
        }
        if (num instanceof Float) {
            float n2 = num.floatValue();
            if (n2 == Float.POSITIVE_INFINITY) {
                return this.floatPositiveInfinity;
            }
            if (n2 == Float.NEGATIVE_INFINITY) {
                return this.floatNegativeInfinity;
            }
            if (Float.isNaN(n2)) {
                return this.floatNaN;
            }
            if (Math.floor(n2) != n2) {
                float absN2 = Math.abs(n2);
                if (absN2 < 0.001f && absN2 > 1.0E-7f) {
                    return new BigDecimal(num.toString()).toString();
                }
            } else if (Math.abs(n2) <= MAX_INCREMENT_1_FLOAT) {
                return Long.toString((long) n2);
            }
            return removeRedundantDot0(Float.toString(n2));
        }
        if (num instanceof BigInteger) {
            return num.toString();
        }
        if (num instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal) num).stripTrailingZeros();
            int scale = bd.scale();
            if (scale <= 0) {
                if (scale <= -100) {
                    return bd.toString();
                }
                return bd.toPlainString();
            }
            return bd.toString();
        }
        return num.toString();
    }

    private static String removeRedundantDot0(String s) {
        int len = s.length();
        int i = 0;
        while (true) {
            if (i >= len) {
                break;
            }
            char c = s.charAt(i);
            if (c != '.') {
                i++;
            } else {
                int i2 = i + 1;
                if (s.charAt(i2) == '0') {
                    int i3 = i2 + 1;
                    if (i3 == len) {
                        return s.substring(0, len - 2);
                    }
                    if (s.charAt(i3) == 'E') {
                        char[] result = new char[s.length() - 2];
                        int dst = 0;
                        for (int src = 0; src < i3 - 2; src++) {
                            int i4 = dst;
                            dst++;
                            result[i4] = s.charAt(src);
                        }
                        for (int src2 = i3; src2 < len; src2++) {
                            int i5 = dst;
                            dst++;
                            result[i5] = s.charAt(src2);
                        }
                        return String.valueOf(result);
                    }
                }
            }
        }
        return s;
    }

    @Override // freemarker.core.TemplateNumberFormat
    public boolean isLocaleBound() {
        return false;
    }

    @Override // freemarker.core.TemplateValueFormat
    public String getDescription() {
        return "c";
    }
}
