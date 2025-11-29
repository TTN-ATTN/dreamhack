package freemarker.template.utility;

import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/NumberUtil.class */
public class NumberUtil {
    private static final BigDecimal BIG_DECIMAL_INT_MIN = BigDecimal.valueOf(-2147483648L);
    private static final BigDecimal BIG_DECIMAL_INT_MAX = BigDecimal.valueOf(2147483647L);
    private static final BigInteger BIG_INTEGER_INT_MIN = BIG_DECIMAL_INT_MIN.toBigInteger();
    private static final BigInteger BIG_INTEGER_INT_MAX = BIG_DECIMAL_INT_MAX.toBigInteger();

    private NumberUtil() {
    }

    public static boolean isInfinite(Number num) {
        if (num instanceof Double) {
            return ((Double) num).isInfinite();
        }
        if (num instanceof Float) {
            return ((Float) num).isInfinite();
        }
        if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
            return false;
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static boolean isNaN(Number num) {
        if (num instanceof Double) {
            return ((Double) num).isNaN();
        }
        if (num instanceof Float) {
            return ((Float) num).isNaN();
        }
        if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
            return false;
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static int getSignum(Number num) throws ArithmeticException {
        if (num instanceof Integer) {
            int n = num.intValue();
            if (n > 0) {
                return 1;
            }
            return n == 0 ? 0 : -1;
        }
        if (num instanceof BigDecimal) {
            return ((BigDecimal) num).signum();
        }
        if (num instanceof Double) {
            double n2 = num.doubleValue();
            if (n2 > 0.0d) {
                return 1;
            }
            if (n2 == 0.0d) {
                return 0;
            }
            if (n2 < 0.0d) {
                return -1;
            }
            throw new ArithmeticException("The signum of " + n2 + " is not defined.");
        }
        if (num instanceof Float) {
            float n3 = num.floatValue();
            if (n3 > 0.0f) {
                return 1;
            }
            if (n3 == 0.0f) {
                return 0;
            }
            if (n3 < 0.0f) {
                return -1;
            }
            throw new ArithmeticException("The signum of " + n3 + " is not defined.");
        }
        if (num instanceof Long) {
            long n4 = num.longValue();
            if (n4 > 0) {
                return 1;
            }
            return n4 == 0 ? 0 : -1;
        }
        if (num instanceof Short) {
            short n5 = num.shortValue();
            if (n5 > 0) {
                return 1;
            }
            return n5 == 0 ? 0 : -1;
        }
        if (num instanceof Byte) {
            byte n6 = num.byteValue();
            if (n6 > 0) {
                return 1;
            }
            return n6 == 0 ? 0 : -1;
        }
        if (num instanceof BigInteger) {
            return ((BigInteger) num).signum();
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static boolean isIntegerBigDecimal(BigDecimal bd) {
        return bd.scale() <= 0 || bd.setScale(0, 1).compareTo(bd) == 0;
    }

    public static boolean hasTypeThatIsKnownToNotSupportInfiniteAndNaN(Number num) {
        return (num instanceof Integer) || (num instanceof BigDecimal) || (num instanceof Long) || (num instanceof Short) || (num instanceof Byte) || (num instanceof BigInteger);
    }

    public static int toIntExact(Number num) {
        if ((num instanceof Integer) || (num instanceof Short) || (num instanceof Byte)) {
            return num.intValue();
        }
        if (num instanceof Long) {
            long n = num.longValue();
            int result = (int) n;
            if (n != result) {
                throw newLossyConverionException(num, Integer.class);
            }
            return result;
        }
        if ((num instanceof Double) || (num instanceof Float)) {
            double n2 = num.doubleValue();
            if (n2 % 1.0d != 0.0d || n2 < -2.147483648E9d || n2 > 2.147483647E9d) {
                throw newLossyConverionException(num, Integer.class);
            }
            return (int) n2;
        }
        if (num instanceof BigDecimal) {
            BigDecimal n3 = (BigDecimal) num;
            if (!isIntegerBigDecimal(n3) || n3.compareTo(BIG_DECIMAL_INT_MAX) > 0 || n3.compareTo(BIG_DECIMAL_INT_MIN) < 0) {
                throw newLossyConverionException(num, Integer.class);
            }
            return n3.intValue();
        }
        if (num instanceof BigInteger) {
            BigInteger n4 = (BigInteger) num;
            if (n4.compareTo(BIG_INTEGER_INT_MAX) > 0 || n4.compareTo(BIG_INTEGER_INT_MIN) < 0) {
                throw newLossyConverionException(num, Integer.class);
            }
            return n4.intValue();
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    private static ArithmeticException newLossyConverionException(Number fromValue, Class toType) {
        return new ArithmeticException("Can't convert " + fromValue + " to type " + ClassUtil.getShortClassName(toType) + " without loss.");
    }
}
