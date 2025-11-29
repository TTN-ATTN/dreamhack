package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.OptimizerUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ArithmeticEngine.class */
public abstract class ArithmeticEngine {
    public static final BigDecimalEngine BIGDECIMAL_ENGINE = new BigDecimalEngine();
    public static final ConservativeEngine CONSERVATIVE_ENGINE = new ConservativeEngine();
    protected int minScale = 12;
    protected int maxScale = 12;
    protected int roundingPolicy = 4;

    public abstract int compareNumbers(Number number, Number number2) throws TemplateException;

    public abstract Number add(Number number, Number number2) throws TemplateException;

    public abstract Number subtract(Number number, Number number2) throws TemplateException;

    public abstract Number multiply(Number number, Number number2) throws TemplateException;

    public abstract Number divide(Number number, Number number2) throws TemplateException;

    public abstract Number modulus(Number number, Number number2) throws TemplateException;

    public abstract Number toNumber(String str);

    public void setMinScale(int minScale) {
        if (minScale < 0) {
            throw new IllegalArgumentException("minScale < 0");
        }
        this.minScale = minScale;
    }

    public void setMaxScale(int maxScale) {
        if (maxScale < this.minScale) {
            throw new IllegalArgumentException("maxScale < minScale");
        }
        this.maxScale = maxScale;
    }

    public void setRoundingPolicy(int roundingPolicy) {
        if (roundingPolicy != 2 && roundingPolicy != 1 && roundingPolicy != 3 && roundingPolicy != 5 && roundingPolicy != 6 && roundingPolicy != 4 && roundingPolicy != 7 && roundingPolicy != 0) {
            throw new IllegalArgumentException("invalid rounding policy");
        }
        this.roundingPolicy = roundingPolicy;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ArithmeticEngine$BigDecimalEngine.class */
    public static class BigDecimalEngine extends ArithmeticEngine {
        @Override // freemarker.core.ArithmeticEngine
        public int compareNumbers(Number first, Number second) throws ArithmeticException {
            int firstSignum = NumberUtil.getSignum(first);
            int secondSignum = NumberUtil.getSignum(second);
            if (firstSignum != secondSignum) {
                if (firstSignum < secondSignum) {
                    return -1;
                }
                return firstSignum > secondSignum ? 1 : 0;
            }
            if (firstSignum == 0 && secondSignum == 0) {
                return 0;
            }
            if (first.getClass() == second.getClass()) {
                if (first instanceof BigDecimal) {
                    return ((BigDecimal) first).compareTo((BigDecimal) second);
                }
                if (first instanceof Integer) {
                    return ((Integer) first).compareTo((Integer) second);
                }
                if (first instanceof Long) {
                    return ((Long) first).compareTo((Long) second);
                }
                if (first instanceof Double) {
                    return ((Double) first).compareTo((Double) second);
                }
                if (first instanceof Float) {
                    return ((Float) first).compareTo((Float) second);
                }
                if (first instanceof Byte) {
                    return ((Byte) first).compareTo((Byte) second);
                }
                if (first instanceof Short) {
                    return ((Short) first).compareTo((Short) second);
                }
            }
            if (first instanceof Double) {
                double firstD = first.doubleValue();
                if (Double.isInfinite(firstD)) {
                    if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                        return firstD == Double.NEGATIVE_INFINITY ? -1 : 1;
                    }
                    if (second instanceof Float) {
                        return Double.compare(firstD, second.doubleValue());
                    }
                }
            }
            if (first instanceof Float) {
                float firstF = first.floatValue();
                if (Float.isInfinite(firstF)) {
                    if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                        return firstF == Float.NEGATIVE_INFINITY ? -1 : 1;
                    }
                    if (second instanceof Double) {
                        return Double.compare(firstF, second.doubleValue());
                    }
                }
            }
            if (second instanceof Double) {
                double secondD = second.doubleValue();
                if (Double.isInfinite(secondD)) {
                    if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                        return secondD == Double.NEGATIVE_INFINITY ? 1 : -1;
                    }
                    if (first instanceof Float) {
                        return Double.compare(first.doubleValue(), secondD);
                    }
                }
            }
            if (second instanceof Float) {
                float secondF = second.floatValue();
                if (Float.isInfinite(secondF)) {
                    if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                        return secondF == Float.NEGATIVE_INFINITY ? 1 : -1;
                    }
                    if (first instanceof Double) {
                        return Double.compare(first.doubleValue(), secondF);
                    }
                }
            }
            return ArithmeticEngine.toBigDecimal(first).compareTo(ArithmeticEngine.toBigDecimal(second));
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number add(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return left.add(right);
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number subtract(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return left.subtract(right);
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number multiply(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            BigDecimal result = left.multiply(right);
            if (result.scale() > this.maxScale) {
                result = result.setScale(this.maxScale, this.roundingPolicy);
            }
            return result;
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number divide(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return divide(left, right);
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number modulus(Number first, Number second) {
            long left = first.longValue();
            long right = second.longValue();
            return Long.valueOf(left % right);
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number toNumber(String s) {
            return ArithmeticEngine.toBigDecimalOrDouble(s);
        }

        private BigDecimal divide(BigDecimal left, BigDecimal right) {
            int scale1 = left.scale();
            int scale2 = right.scale();
            int scale = Math.max(scale1, scale2);
            return left.divide(right, Math.max(this.minScale, scale), this.roundingPolicy);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ArithmeticEngine$ConservativeEngine.class */
    public static class ConservativeEngine extends ArithmeticEngine {
        private static final int INTEGER = 0;
        private static final int LONG = 1;
        private static final int FLOAT = 2;
        private static final int DOUBLE = 3;
        private static final int BIGINTEGER = 4;
        private static final int BIGDECIMAL = 5;
        private static final Map classCodes = createClassCodesMap();

        @Override // freemarker.core.ArithmeticEngine
        public int compareNumbers(Number first, Number second) throws TemplateException {
            switch (getCommonClassCode(first, second)) {
                case 0:
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    if (n1 < n2) {
                        return -1;
                    }
                    return n1 == n2 ? 0 : 1;
                case 1:
                    long n12 = first.longValue();
                    long n22 = second.longValue();
                    if (n12 < n22) {
                        return -1;
                    }
                    return n12 == n22 ? 0 : 1;
                case 2:
                    float n13 = first.floatValue();
                    float n23 = second.floatValue();
                    if (n13 < n23) {
                        return -1;
                    }
                    return n13 == n23 ? 0 : 1;
                case 3:
                    double n14 = first.doubleValue();
                    double n24 = second.doubleValue();
                    if (n14 < n24) {
                        return -1;
                    }
                    return n14 == n24 ? 0 : 1;
                case 4:
                    BigInteger n15 = toBigInteger(first);
                    BigInteger n25 = toBigInteger(second);
                    return n15.compareTo(n25);
                case 5:
                    BigDecimal n16 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n26 = ArithmeticEngine.toBigDecimal(second);
                    return n16.compareTo(n26);
                default:
                    throw new Error();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number add(Number first, Number second) throws TemplateException {
            long jIntValue;
            switch (getCommonClassCode(first, second)) {
                case 0:
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 + n2;
                    if ((n ^ n1) < 0 && (n ^ n2) < 0) {
                        jIntValue = Long.valueOf(n1 + n2).longValue();
                    } else {
                        jIntValue = Integer.valueOf(n).intValue();
                    }
                    return Long.valueOf(jIntValue);
                case 1:
                    long n12 = first.longValue();
                    long n22 = second.longValue();
                    long n3 = n12 + n22;
                    if ((n3 ^ n12) < 0 && (n3 ^ n22) < 0) {
                        return toBigInteger(first).add(toBigInteger(second));
                    }
                    return Long.valueOf(n3);
                case 2:
                    return Float.valueOf(first.floatValue() + second.floatValue());
                case 3:
                    return Double.valueOf(first.doubleValue() + second.doubleValue());
                case 4:
                    BigInteger n13 = toBigInteger(first);
                    BigInteger n23 = toBigInteger(second);
                    return n13.add(n23);
                case 5:
                    BigDecimal n14 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n24 = ArithmeticEngine.toBigDecimal(second);
                    return n14.add(n24);
                default:
                    throw new Error();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number subtract(Number first, Number second) throws TemplateException {
            long jIntValue;
            switch (getCommonClassCode(first, second)) {
                case 0:
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 - n2;
                    if ((n ^ n1) < 0 && (n ^ (n2 ^ (-1))) < 0) {
                        jIntValue = Long.valueOf(n1 - n2).longValue();
                    } else {
                        jIntValue = Integer.valueOf(n).intValue();
                    }
                    return Long.valueOf(jIntValue);
                case 1:
                    long n12 = first.longValue();
                    long n22 = second.longValue();
                    long n3 = n12 - n22;
                    if ((n3 ^ n12) < 0 && (n3 ^ (n22 ^ (-1))) < 0) {
                        return toBigInteger(first).subtract(toBigInteger(second));
                    }
                    return Long.valueOf(n3);
                case 2:
                    return Float.valueOf(first.floatValue() - second.floatValue());
                case 3:
                    return Double.valueOf(first.doubleValue() - second.doubleValue());
                case 4:
                    BigInteger n13 = toBigInteger(first);
                    BigInteger n23 = toBigInteger(second);
                    return n13.subtract(n23);
                case 5:
                    BigDecimal n14 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n24 = ArithmeticEngine.toBigDecimal(second);
                    return n14.subtract(n24);
                default:
                    throw new Error();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number multiply(Number first, Number second) throws TemplateException {
            long jIntValue;
            switch (getCommonClassCode(first, second)) {
                case 0:
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 * n2;
                    if (n1 == 0 || n / n1 == n2) {
                        jIntValue = Integer.valueOf(n).intValue();
                    } else {
                        jIntValue = Long.valueOf(n1 * n2).longValue();
                    }
                    return Long.valueOf(jIntValue);
                case 1:
                    long n12 = first.longValue();
                    long n22 = second.longValue();
                    long n3 = n12 * n22;
                    if (n12 == 0 || n3 / n12 == n22) {
                        return Long.valueOf(n3);
                    }
                    return toBigInteger(first).multiply(toBigInteger(second));
                case 2:
                    return Float.valueOf(first.floatValue() * second.floatValue());
                case 3:
                    return Double.valueOf(first.doubleValue() * second.doubleValue());
                case 4:
                    BigInteger n13 = toBigInteger(first);
                    BigInteger n23 = toBigInteger(second);
                    return n13.multiply(n23);
                case 5:
                    BigDecimal n14 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n24 = ArithmeticEngine.toBigDecimal(second);
                    BigDecimal r = n14.multiply(n24);
                    return r.scale() > this.maxScale ? r.setScale(this.maxScale, this.roundingPolicy) : r;
                default:
                    throw new Error();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number divide(Number first, Number second) throws TemplateException {
            switch (getCommonClassCode(first, second)) {
                case 0:
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    if (n1 % n2 == 0) {
                        return Integer.valueOf(n1 / n2);
                    }
                    return Double.valueOf(n1 / n2);
                case 1:
                    long n12 = first.longValue();
                    long n22 = second.longValue();
                    if (n12 % n22 == 0) {
                        return Long.valueOf(n12 / n22);
                    }
                    return Double.valueOf(n12 / n22);
                case 2:
                    return Float.valueOf(first.floatValue() / second.floatValue());
                case 3:
                    return Double.valueOf(first.doubleValue() / second.doubleValue());
                case 4:
                    BigInteger n13 = toBigInteger(first);
                    BigInteger n23 = toBigInteger(second);
                    BigInteger[] divmod = n13.divideAndRemainder(n23);
                    if (divmod[1].equals(BigInteger.ZERO)) {
                        return divmod[0];
                    }
                    BigDecimal bd1 = new BigDecimal(n13);
                    BigDecimal bd2 = new BigDecimal(n23);
                    return bd1.divide(bd2, this.minScale, this.roundingPolicy);
                case 5:
                    BigDecimal n14 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n24 = ArithmeticEngine.toBigDecimal(second);
                    int scale1 = n14.scale();
                    int scale2 = n24.scale();
                    int scale = Math.max(scale1, scale2);
                    return n14.divide(n24, Math.max(this.minScale, scale), this.roundingPolicy);
                default:
                    throw new Error();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number modulus(Number first, Number second) throws TemplateException {
            switch (getCommonClassCode(first, second)) {
                case 0:
                    return Integer.valueOf(first.intValue() % second.intValue());
                case 1:
                    return Long.valueOf(first.longValue() % second.longValue());
                case 2:
                    return Float.valueOf(first.floatValue() % second.floatValue());
                case 3:
                    return Double.valueOf(first.doubleValue() % second.doubleValue());
                case 4:
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.mod(n2);
                case 5:
                    throw new _MiscTemplateException("Can't calculate remainder on BigDecimals");
                default:
                    throw new BugException();
            }
        }

        @Override // freemarker.core.ArithmeticEngine
        public Number toNumber(String s) {
            Number n = ArithmeticEngine.toBigDecimalOrDouble(s);
            return n instanceof BigDecimal ? OptimizerUtil.optimizeNumberRepresentation(n) : n;
        }

        private static Map createClassCodesMap() {
            Map map = new HashMap(17);
            map.put(Byte.class, 0);
            map.put(Short.class, 0);
            map.put(Integer.class, 0);
            map.put(Long.class, 1);
            map.put(Float.class, 2);
            map.put(Double.class, 3);
            map.put(BigInteger.class, 4);
            map.put(BigDecimal.class, 5);
            return map;
        }

        private static int getClassCode(Number num) throws TemplateException {
            try {
                return ((Integer) classCodes.get(num.getClass())).intValue();
            } catch (NullPointerException e) {
                if (num == null) {
                    throw new _MiscTemplateException("The Number object was null.");
                }
                throw new _MiscTemplateException("Unknown number type ", num.getClass().getName());
            }
        }

        private static int getCommonClassCode(Number num1, Number num2) throws TemplateException {
            int c1 = getClassCode(num1);
            int c2 = getClassCode(num2);
            int c = c1 > c2 ? c1 : c2;
            switch (c) {
                case 2:
                    if ((c1 < c2 ? c1 : c2) == 1) {
                        return 3;
                    }
                    break;
                case 4:
                    int min = c1 < c2 ? c1 : c2;
                    if (min == 3 || min == 2) {
                        return 5;
                    }
                    break;
            }
            return c;
        }

        private static BigInteger toBigInteger(Number num) {
            return num instanceof BigInteger ? (BigInteger) num : new BigInteger(num.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        }
        if ((num instanceof Integer) || (num instanceof Long) || (num instanceof Byte) || (num instanceof Short)) {
            return BigDecimal.valueOf(num.longValue());
        }
        if (num instanceof BigInteger) {
            return new BigDecimal((BigInteger) num);
        }
        try {
            return new BigDecimal(num.toString());
        } catch (NumberFormatException e) {
            if (NumberUtil.isInfinite(num)) {
                throw new NumberFormatException("It's impossible to convert an infinite value (" + num.getClass().getSimpleName() + " " + num + ") to BigDecimal.");
            }
            throw new NumberFormatException("Can't parse this as BigDecimal number: " + StringUtil.jQuote(num));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Number toBigDecimalOrDouble(String s) {
        if (s.length() > 2) {
            char c = s.charAt(0);
            if (c == 'I' && (s.equals("INF") || s.equals("Infinity"))) {
                return Double.valueOf(Double.POSITIVE_INFINITY);
            }
            if (c == 'N' && s.equals("NaN")) {
                return Double.valueOf(Double.NaN);
            }
            if (c == '-' && s.charAt(1) == 'I' && (s.equals("-INF") || s.equals("-Infinity"))) {
                return Double.valueOf(Double.NEGATIVE_INFINITY);
            }
        }
        return new BigDecimal(s);
    }
}
