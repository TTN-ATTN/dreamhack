package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NumberUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.tomcat.jni.Status;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil.class */
class OverloadedNumberUtil {
    static final int BIG_MANTISSA_LOSS_PRICE = 40000;
    private static final long MAX_DOUBLE_OR_LONG = 9007199254740992L;
    private static final long MIN_DOUBLE_OR_LONG = -9007199254740992L;
    private static final int MAX_DOUBLE_OR_LONG_LOG_2 = 53;
    private static final int MAX_FLOAT_OR_INT = 16777216;
    private static final int MIN_FLOAT_OR_INT = -16777216;
    private static final int MAX_FLOAT_OR_INT_LOG_2 = 24;
    private static final double LOWEST_ABOVE_ZERO = 1.0E-6d;
    private static final double HIGHEST_BELOW_ONE = 0.999999d;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigDecimalSource.class */
    interface BigDecimalSource {
        BigDecimal bigDecimalValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerSource.class */
    interface BigIntegerSource {
        BigInteger bigIntegerValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$ByteSource.class */
    interface ByteSource {
        Byte byteValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleSource.class */
    interface DoubleSource {
        Double doubleValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$FloatSource.class */
    interface FloatSource {
        Float floatValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$IntegerSource.class */
    interface IntegerSource {
        Integer integerValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$LongSource.class */
    interface LongSource {
        Long longValue();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$ShortSource.class */
    interface ShortSource {
        Short shortValue();
    }

    private OverloadedNumberUtil() {
    }

    static Number addFallbackType(Number num, int typeFlags) {
        boolean exact;
        boolean exact2;
        Class numClass = num.getClass();
        if (numClass == BigDecimal.class) {
            BigDecimal n = (BigDecimal) num;
            if ((typeFlags & 316) != 0 && (typeFlags & 704) != 0 && NumberUtil.isIntegerBigDecimal(n)) {
                return new IntegerBigDecimal(n);
            }
            return n;
        }
        if (numClass == Integer.class) {
            int pn = num.intValue();
            if ((typeFlags & 4) != 0 && pn <= 127 && pn >= -128) {
                return new IntegerOrByte((Integer) num, (byte) pn);
            }
            if ((typeFlags & 8) != 0 && pn <= 32767 && pn >= -32768) {
                return new IntegerOrShort((Integer) num, (short) pn);
            }
            return num;
        }
        if (numClass == Long.class) {
            long pn2 = num.longValue();
            if ((typeFlags & 4) != 0 && pn2 <= 127 && pn2 >= -128) {
                return new LongOrByte((Long) num, (byte) pn2);
            }
            if ((typeFlags & 8) != 0 && pn2 <= 32767 && pn2 >= -32768) {
                return new LongOrShort((Long) num, (short) pn2);
            }
            if ((typeFlags & 16) != 0 && pn2 <= 2147483647L && pn2 >= -2147483648L) {
                return new LongOrInteger((Long) num, (int) pn2);
            }
            return num;
        }
        if (numClass == Double.class) {
            double doubleN = num.doubleValue();
            if ((typeFlags & 316) != 0 && doubleN <= 9.007199254740992E15d && doubleN >= -9.007199254740992E15d) {
                long longN = num.longValue();
                double diff = doubleN - longN;
                if (diff == 0.0d) {
                    exact2 = true;
                } else if (diff > 0.0d) {
                    if (diff < LOWEST_ABOVE_ZERO) {
                        exact2 = false;
                    } else if (diff > HIGHEST_BELOW_ONE) {
                        exact2 = false;
                        longN++;
                    }
                } else if (diff > -1.0E-6d) {
                    exact2 = false;
                } else if (diff < -0.999999d) {
                    exact2 = false;
                    longN--;
                }
                if ((typeFlags & 4) != 0 && longN <= 127 && longN >= -128) {
                    return new DoubleOrByte((Double) num, (byte) longN);
                }
                if ((typeFlags & 8) != 0 && longN <= 32767 && longN >= -32768) {
                    return new DoubleOrShort((Double) num, (short) longN);
                }
                if ((typeFlags & 16) != 0 && longN <= 2147483647L && longN >= -2147483648L) {
                    int intN = (int) longN;
                    return ((typeFlags & 64) == 0 || intN < MIN_FLOAT_OR_INT || intN > 16777216) ? new DoubleOrInteger((Double) num, intN) : new DoubleOrIntegerOrFloat((Double) num, intN);
                }
                if ((typeFlags & 32) != 0) {
                    if (exact2) {
                        return new DoubleOrLong((Double) num, longN);
                    }
                    if (longN >= -2147483648L && longN <= 2147483647L) {
                        return new DoubleOrLong((Double) num, longN);
                    }
                }
            }
            if ((typeFlags & 64) != 0 && doubleN >= -3.4028234663852886E38d && doubleN <= 3.4028234663852886E38d) {
                return new DoubleOrFloat((Double) num);
            }
            return num;
        }
        if (numClass == Float.class) {
            float floatN = num.floatValue();
            if ((typeFlags & 316) != 0 && floatN <= 1.6777216E7f && floatN >= -1.6777216E7f) {
                int intN2 = num.intValue();
                double diff2 = floatN - intN2;
                if (diff2 == 0.0d) {
                    exact = true;
                } else if (intN2 >= -128 && intN2 <= 127) {
                    if (diff2 > 0.0d) {
                        if (diff2 < 1.0E-5d) {
                            exact = false;
                        } else if (diff2 > 0.99999d) {
                            exact = false;
                            intN2++;
                        }
                    } else if (diff2 > -1.0E-5d) {
                        exact = false;
                    } else if (diff2 < -0.99999d) {
                        exact = false;
                        intN2--;
                    }
                }
                if ((typeFlags & 4) != 0 && intN2 <= 127 && intN2 >= -128) {
                    return new FloatOrByte((Float) num, (byte) intN2);
                }
                if ((typeFlags & 8) != 0 && intN2 <= 32767 && intN2 >= -32768) {
                    return new FloatOrShort((Float) num, (short) intN2);
                }
                if ((typeFlags & 16) != 0) {
                    return new FloatOrInteger((Float) num, intN2);
                }
                if ((typeFlags & 32) != 0) {
                    return exact ? new FloatOrInteger((Float) num, intN2) : new FloatOrByte((Float) num, (byte) intN2);
                }
            }
            return num;
        }
        if (numClass == Byte.class) {
            return num;
        }
        if (numClass == Short.class) {
            short pn3 = num.shortValue();
            if ((typeFlags & 4) != 0 && pn3 <= 127 && pn3 >= -128) {
                return new ShortOrByte((Short) num, (byte) pn3);
            }
            return num;
        }
        if (numClass == BigInteger.class) {
            if ((typeFlags & 252) != 0) {
                BigInteger biNum = (BigInteger) num;
                int bitLength = biNum.bitLength();
                if ((typeFlags & 4) != 0 && bitLength <= 7) {
                    return new BigIntegerOrByte(biNum);
                }
                if ((typeFlags & 8) != 0 && bitLength <= 15) {
                    return new BigIntegerOrShort(biNum);
                }
                if ((typeFlags & 16) != 0 && bitLength <= 31) {
                    return new BigIntegerOrInteger(biNum);
                }
                if ((typeFlags & 32) != 0 && bitLength <= 63) {
                    return new BigIntegerOrLong(biNum);
                }
                if ((typeFlags & 64) != 0 && (bitLength <= 24 || (bitLength == 25 && biNum.getLowestSetBit() >= 24))) {
                    return new BigIntegerOrFloat(biNum);
                }
                if ((typeFlags & 128) != 0 && (bitLength <= 53 || (bitLength == 54 && biNum.getLowestSetBit() >= 53))) {
                    return new BigIntegerOrDouble(biNum);
                }
                return num;
            }
            return num;
        }
        return num;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$NumberWithFallbackType.class */
    static abstract class NumberWithFallbackType extends Number implements Comparable {
        protected abstract Number getSourceNumber();

        NumberWithFallbackType() {
        }

        @Override // java.lang.Number
        public int intValue() {
            return getSourceNumber().intValue();
        }

        @Override // java.lang.Number
        public long longValue() {
            return getSourceNumber().longValue();
        }

        @Override // java.lang.Number
        public float floatValue() {
            return getSourceNumber().floatValue();
        }

        @Override // java.lang.Number
        public double doubleValue() {
            return getSourceNumber().doubleValue();
        }

        @Override // java.lang.Number
        public byte byteValue() {
            return getSourceNumber().byteValue();
        }

        @Override // java.lang.Number
        public short shortValue() {
            return getSourceNumber().shortValue();
        }

        public int hashCode() {
            return getSourceNumber().hashCode();
        }

        public boolean equals(Object obj) {
            if (obj != null && getClass() == obj.getClass()) {
                return getSourceNumber().equals(((NumberWithFallbackType) obj).getSourceNumber());
            }
            return false;
        }

        public String toString() {
            return getSourceNumber().toString();
        }

        @Override // java.lang.Comparable
        public int compareTo(Object o) {
            Object sourceNumber = getSourceNumber();
            if (sourceNumber instanceof Comparable) {
                return ((Comparable) sourceNumber).compareTo(o);
            }
            throw new ClassCastException(sourceNumber.getClass().getName() + " is not Comparable.");
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$IntegerBigDecimal.class */
    static final class IntegerBigDecimal extends NumberWithFallbackType {
        private final BigDecimal n;

        IntegerBigDecimal(BigDecimal n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        public BigInteger bigIntegerValue() {
            return this.n.toBigInteger();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$LongOrSmallerInteger.class */
    static abstract class LongOrSmallerInteger extends NumberWithFallbackType {
        private final Long n;

        protected LongOrSmallerInteger(Long n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.n.longValue();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$LongOrByte.class */
    static class LongOrByte extends LongOrSmallerInteger {
        private final byte w;

        LongOrByte(Long n, byte w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public byte byteValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$LongOrShort.class */
    static class LongOrShort extends LongOrSmallerInteger {
        private final short w;

        LongOrShort(Long n, short w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$LongOrInteger.class */
    static class LongOrInteger extends LongOrSmallerInteger {
        private final int w;

        LongOrInteger(Long n, int w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$IntegerOrSmallerInteger.class */
    static abstract class IntegerOrSmallerInteger extends NumberWithFallbackType {
        private final Integer n;

        protected IntegerOrSmallerInteger(Integer n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.n.intValue();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$IntegerOrByte.class */
    static class IntegerOrByte extends IntegerOrSmallerInteger {
        private final byte w;

        IntegerOrByte(Integer n, byte w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public byte byteValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$IntegerOrShort.class */
    static class IntegerOrShort extends IntegerOrSmallerInteger {
        private final short w;

        IntegerOrShort(Integer n, short w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$ShortOrByte.class */
    static class ShortOrByte extends NumberWithFallbackType {
        private final Short n;
        private final byte w;

        protected ShortOrByte(Short n, byte w) {
            this.n = n;
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.n.shortValue();
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public byte byteValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrWholeNumber.class */
    static abstract class DoubleOrWholeNumber extends NumberWithFallbackType {
        private final Double n;

        protected DoubleOrWholeNumber(Double n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public double doubleValue() {
            return this.n.doubleValue();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrByte.class */
    static final class DoubleOrByte extends DoubleOrWholeNumber {
        private final byte w;

        DoubleOrByte(Double n, byte w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public byte byteValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrShort.class */
    static final class DoubleOrShort extends DoubleOrWholeNumber {
        private final short w;

        DoubleOrShort(Double n, short w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrIntegerOrFloat.class */
    static final class DoubleOrIntegerOrFloat extends DoubleOrWholeNumber {
        private final int w;

        DoubleOrIntegerOrFloat(Double n, int w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrInteger.class */
    static final class DoubleOrInteger extends DoubleOrWholeNumber {
        private final int w;

        DoubleOrInteger(Double n, int w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrLong.class */
    static final class DoubleOrLong extends DoubleOrWholeNumber {
        private final long w;

        DoubleOrLong(Double n, long w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$DoubleOrFloat.class */
    static final class DoubleOrFloat extends NumberWithFallbackType {
        private final Double n;

        DoubleOrFloat(Double n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public float floatValue() {
            return this.n.floatValue();
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public double doubleValue() {
            return this.n.doubleValue();
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$FloatOrWholeNumber.class */
    static abstract class FloatOrWholeNumber extends NumberWithFallbackType {
        private final Float n;

        FloatOrWholeNumber(Float n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public float floatValue() {
            return this.n.floatValue();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$FloatOrByte.class */
    static final class FloatOrByte extends FloatOrWholeNumber {
        private final byte w;

        FloatOrByte(Float n, byte w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public byte byteValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$FloatOrShort.class */
    static final class FloatOrShort extends FloatOrWholeNumber {
        private final short w;

        FloatOrShort(Float n, short w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public short shortValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$FloatOrInteger.class */
    static final class FloatOrInteger extends FloatOrWholeNumber {
        private final int w;

        FloatOrInteger(Float n, int w) {
            super(n);
            this.w = w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public int intValue() {
            return this.w;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public long longValue() {
            return this.w;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrPrimitive.class */
    static abstract class BigIntegerOrPrimitive extends NumberWithFallbackType {
        protected final BigInteger n;

        BigIntegerOrPrimitive(BigInteger n) {
            this.n = n;
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType
        protected Number getSourceNumber() {
            return this.n;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrByte.class */
    static final class BigIntegerOrByte extends BigIntegerOrPrimitive {
        BigIntegerOrByte(BigInteger n) {
            super(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrShort.class */
    static final class BigIntegerOrShort extends BigIntegerOrPrimitive {
        BigIntegerOrShort(BigInteger n) {
            super(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrInteger.class */
    static final class BigIntegerOrInteger extends BigIntegerOrPrimitive {
        BigIntegerOrInteger(BigInteger n) {
            super(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrLong.class */
    static final class BigIntegerOrLong extends BigIntegerOrPrimitive {
        BigIntegerOrLong(BigInteger n) {
            super(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrFPPrimitive.class */
    static abstract class BigIntegerOrFPPrimitive extends BigIntegerOrPrimitive {
        BigIntegerOrFPPrimitive(BigInteger n) {
            super(n);
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public float floatValue() {
            return this.n.longValue();
        }

        @Override // freemarker.ext.beans.OverloadedNumberUtil.NumberWithFallbackType, java.lang.Number
        public double doubleValue() {
            return this.n.longValue();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrFloat.class */
    static final class BigIntegerOrFloat extends BigIntegerOrFPPrimitive {
        BigIntegerOrFloat(BigInteger n) {
            super(n);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrDouble.class */
    static final class BigIntegerOrDouble extends BigIntegerOrFPPrimitive {
        BigIntegerOrDouble(BigInteger n) {
            super(n);
        }
    }

    static int getArgumentConversionPrice(Class fromC, Class toC) {
        if (toC == fromC) {
            return 0;
        }
        if (toC == Integer.class) {
            if (fromC == IntegerBigDecimal.class) {
                return 31003;
            }
            if (fromC == BigDecimal.class) {
                return 41003;
            }
            if (fromC == Long.class || fromC == Double.class || fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10003;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 21003;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class) {
                return 22003;
            }
            if (fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 0;
            }
            if (fromC == DoubleOrByte.class) {
                return 22003;
            }
            if (fromC == LongOrByte.class) {
                return 21003;
            }
            if (fromC == Short.class) {
                return 10003;
            }
            if (fromC == LongOrShort.class) {
                return 21003;
            }
            if (fromC == ShortOrByte.class) {
                return 10003;
            }
            if (fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 21003;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 16003;
            }
            if (fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 16003;
            }
            if (fromC == IntegerOrShort.class) {
                return 0;
            }
            if (fromC == DoubleOrShort.class) {
                return 22003;
            }
            return fromC == BigIntegerOrShort.class ? 16003 : Integer.MAX_VALUE;
        }
        if (toC == Long.class) {
            if (fromC == Integer.class) {
                return 10004;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 31004;
            }
            if (fromC == BigDecimal.class) {
                return 41004;
            }
            if (fromC == Double.class || fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10004;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 0;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class) {
                return 21004;
            }
            if (fromC == IntegerOrByte.class) {
                return 10004;
            }
            if (fromC == DoubleOrByte.class) {
                return 21004;
            }
            if (fromC == LongOrByte.class) {
                return 0;
            }
            if (fromC == Short.class) {
                return 10004;
            }
            if (fromC == LongOrShort.class) {
                return 0;
            }
            if (fromC == ShortOrByte.class) {
                return 10004;
            }
            if (fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 21004;
            }
            if (fromC == BigIntegerOrInteger.class || fromC == BigIntegerOrLong.class) {
                return 15004;
            }
            if (fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 15004;
            }
            if (fromC == IntegerOrShort.class) {
                return 10004;
            }
            if (fromC == DoubleOrShort.class) {
                return 21004;
            }
            return fromC == BigIntegerOrShort.class ? 15004 : Integer.MAX_VALUE;
        }
        if (toC == Double.class) {
            if (fromC == Integer.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == IntegerBigDecimal.class || fromC == BigDecimal.class) {
                return 32007;
            }
            if (fromC == Long.class) {
                return 30007;
            }
            if (fromC == Float.class) {
                return 10007;
            }
            if (fromC == Byte.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 21007;
            }
            if (fromC == DoubleOrFloat.class || fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class) {
                return 0;
            }
            if (fromC == IntegerOrByte.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == DoubleOrByte.class) {
                return 0;
            }
            if (fromC == LongOrByte.class) {
                return 21007;
            }
            if (fromC == Short.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == LongOrShort.class) {
                return 21007;
            }
            if (fromC == ShortOrByte.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 10007;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 30007;
            }
            if (fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class || fromC == BigIntegerOrByte.class || fromC == IntegerOrShort.class) {
                return Status.APR_ENOTIME;
            }
            if (fromC == DoubleOrShort.class) {
                return 0;
            }
            if (fromC == BigIntegerOrShort.class) {
                return Status.APR_ENOTIME;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Float.class) {
            if (fromC == Integer.class) {
                return 30006;
            }
            if (fromC == IntegerBigDecimal.class || fromC == BigDecimal.class) {
                return 33006;
            }
            if (fromC == Long.class) {
                return 40006;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return Status.APR_ENOPROC;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class || fromC == DoubleOrFloat.class) {
                return 30006;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 23006;
            }
            if (fromC == DoubleOrInteger.class) {
                return 30006;
            }
            if (fromC == DoubleOrLong.class) {
                return 40006;
            }
            if (fromC == IntegerOrByte.class) {
                return 24006;
            }
            if (fromC == DoubleOrByte.class) {
                return 23006;
            }
            if (fromC == LongOrByte.class) {
                return 24006;
            }
            if (fromC == Short.class) {
                return Status.APR_ENOPROC;
            }
            if (fromC == LongOrShort.class) {
                return 24006;
            }
            if (fromC == ShortOrByte.class) {
                return Status.APR_ENOPROC;
            }
            if (fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 0;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 30006;
            }
            if (fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class) {
                return 40006;
            }
            if (fromC == BigIntegerOrFloat.class || fromC == BigIntegerOrByte.class || fromC == IntegerOrShort.class) {
                return 24006;
            }
            if (fromC == DoubleOrShort.class) {
                return 23006;
            }
            return fromC == BigIntegerOrShort.class ? 24006 : Integer.MAX_VALUE;
        }
        if (toC == Byte.class) {
            if (fromC == Integer.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 35001;
            }
            if (fromC == BigDecimal.class) {
                return 45001;
            }
            if (fromC == Long.class || fromC == Double.class || fromC == Float.class || fromC == BigInteger.class || fromC == LongOrInteger.class || fromC == DoubleOrFloat.class || fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 22001;
            }
            if (fromC == DoubleOrByte.class) {
                return 25001;
            }
            if (fromC == LongOrByte.class) {
                return 23001;
            }
            if (fromC == Short.class || fromC == LongOrShort.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == ShortOrByte.class) {
                return 21001;
            }
            if (fromC == FloatOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == FloatOrByte.class) {
                return 23001;
            }
            if (fromC == FloatOrShort.class || fromC == BigIntegerOrInteger.class || fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 18001;
            }
            return (fromC == IntegerOrShort.class || fromC == DoubleOrShort.class || fromC != BigIntegerOrShort.class) ? Integer.MAX_VALUE : Integer.MAX_VALUE;
        }
        if (toC == Short.class) {
            if (fromC == Integer.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 34002;
            }
            if (fromC == BigDecimal.class) {
                return 44002;
            }
            if (fromC == Long.class || fromC == Double.class || fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10002;
            }
            if (fromC == BigInteger.class || fromC == LongOrInteger.class || fromC == DoubleOrFloat.class || fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 21002;
            }
            if (fromC == DoubleOrByte.class) {
                return 24002;
            }
            if (fromC == LongOrByte.class || fromC == LongOrShort.class) {
                return 22002;
            }
            if (fromC == ShortOrByte.class) {
                return 0;
            }
            if (fromC == FloatOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 22002;
            }
            if (fromC == BigIntegerOrInteger.class || fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 17002;
            }
            if (fromC == IntegerOrShort.class) {
                return 21002;
            }
            if (fromC == DoubleOrShort.class) {
                return 24002;
            }
            return fromC == BigIntegerOrShort.class ? 17002 : Integer.MAX_VALUE;
        }
        if (toC == BigDecimal.class) {
            if (fromC == Integer.class) {
                return Status.APR_ENODIR;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 0;
            }
            if (fromC == Long.class || fromC == Double.class || fromC == Float.class || fromC == Byte.class) {
                return Status.APR_ENODIR;
            }
            if (fromC == BigInteger.class) {
                return 10008;
            }
            if (fromC == LongOrInteger.class || fromC == DoubleOrFloat.class || fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class || fromC == IntegerOrByte.class || fromC == DoubleOrByte.class || fromC == LongOrByte.class || fromC == Short.class || fromC == LongOrShort.class || fromC == ShortOrByte.class || fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return Status.APR_ENODIR;
            }
            if (fromC == BigIntegerOrInteger.class || fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class || fromC == BigIntegerOrByte.class) {
                return 10008;
            }
            return (fromC == IntegerOrShort.class || fromC == DoubleOrShort.class) ? Status.APR_ENODIR : fromC == BigIntegerOrShort.class ? 10008 : Integer.MAX_VALUE;
        }
        if (toC == BigInteger.class) {
            if (fromC == Integer.class || fromC == IntegerBigDecimal.class) {
                return 10005;
            }
            if (fromC == BigDecimal.class) {
                return 40005;
            }
            if (fromC == Long.class) {
                return 10005;
            }
            if (fromC == Double.class || fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class || fromC == LongOrInteger.class) {
                return 10005;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class || fromC == DoubleOrInteger.class || fromC == DoubleOrLong.class) {
                return 21005;
            }
            if (fromC == IntegerOrByte.class) {
                return 10005;
            }
            if (fromC == DoubleOrByte.class) {
                return 21005;
            }
            if (fromC == LongOrByte.class || fromC == Short.class || fromC == LongOrShort.class || fromC == ShortOrByte.class) {
                return 10005;
            }
            if (fromC == FloatOrInteger.class || fromC == FloatOrByte.class || fromC == FloatOrShort.class) {
                return 25005;
            }
            if (fromC == BigIntegerOrInteger.class || fromC == BigIntegerOrLong.class || fromC == BigIntegerOrDouble.class || fromC == BigIntegerOrFloat.class || fromC == BigIntegerOrByte.class) {
                return 0;
            }
            if (fromC == IntegerOrShort.class) {
                return 10005;
            }
            if (fromC == DoubleOrShort.class) {
                return 21005;
            }
            return fromC == BigIntegerOrShort.class ? 0 : Integer.MAX_VALUE;
        }
        return Integer.MAX_VALUE;
    }

    static int compareNumberTypeSpecificity(Class c1, Class c2) {
        Class c12 = ClassUtil.primitiveClassToBoxingClass(c1);
        Class c22 = ClassUtil.primitiveClassToBoxingClass(c2);
        if (c12 == c22) {
            return 0;
        }
        if (c12 == Integer.class) {
            if (c22 == Long.class) {
                return 1;
            }
            if (c22 == Double.class) {
                return 4;
            }
            if (c22 == Float.class) {
                return 3;
            }
            if (c22 == Byte.class) {
                return -2;
            }
            if (c22 == Short.class) {
                return -1;
            }
            if (c22 == BigDecimal.class) {
                return 5;
            }
            return c22 == BigInteger.class ? 2 : 0;
        }
        if (c12 == Long.class) {
            if (c22 == Integer.class) {
                return -1;
            }
            if (c22 == Double.class) {
                return 3;
            }
            if (c22 == Float.class) {
                return 2;
            }
            if (c22 == Byte.class) {
                return -3;
            }
            if (c22 == Short.class) {
                return -2;
            }
            if (c22 == BigDecimal.class) {
                return 4;
            }
            return c22 == BigInteger.class ? 1 : 0;
        }
        if (c12 == Double.class) {
            if (c22 == Integer.class) {
                return -4;
            }
            if (c22 == Long.class) {
                return -3;
            }
            if (c22 == Float.class) {
                return -1;
            }
            if (c22 == Byte.class) {
                return -6;
            }
            if (c22 == Short.class) {
                return -5;
            }
            if (c22 == BigDecimal.class) {
                return 1;
            }
            return c22 == BigInteger.class ? -2 : 0;
        }
        if (c12 == Float.class) {
            if (c22 == Integer.class) {
                return -3;
            }
            if (c22 == Long.class) {
                return -2;
            }
            if (c22 == Double.class) {
                return 1;
            }
            if (c22 == Byte.class) {
                return -5;
            }
            if (c22 == Short.class) {
                return -4;
            }
            if (c22 == BigDecimal.class) {
                return 2;
            }
            return c22 == BigInteger.class ? -1 : 0;
        }
        if (c12 == Byte.class) {
            if (c22 == Integer.class) {
                return 2;
            }
            if (c22 == Long.class) {
                return 3;
            }
            if (c22 == Double.class) {
                return 6;
            }
            if (c22 == Float.class) {
                return 5;
            }
            if (c22 == Short.class) {
                return 1;
            }
            if (c22 == BigDecimal.class) {
                return 7;
            }
            return c22 == BigInteger.class ? 4 : 0;
        }
        if (c12 == Short.class) {
            if (c22 == Integer.class) {
                return 1;
            }
            if (c22 == Long.class) {
                return 2;
            }
            if (c22 == Double.class) {
                return 5;
            }
            if (c22 == Float.class) {
                return 4;
            }
            if (c22 == Byte.class) {
                return -1;
            }
            if (c22 == BigDecimal.class) {
                return 6;
            }
            return c22 == BigInteger.class ? 3 : 0;
        }
        if (c12 == BigDecimal.class) {
            if (c22 == Integer.class) {
                return -5;
            }
            if (c22 == Long.class) {
                return -4;
            }
            if (c22 == Double.class) {
                return -1;
            }
            if (c22 == Float.class) {
                return -2;
            }
            if (c22 == Byte.class) {
                return -7;
            }
            if (c22 == Short.class) {
                return -6;
            }
            return c22 == BigInteger.class ? -3 : 0;
        }
        if (c12 == BigInteger.class) {
            if (c22 == Integer.class) {
                return -2;
            }
            if (c22 == Long.class) {
                return -1;
            }
            if (c22 == Double.class) {
                return 2;
            }
            if (c22 == Float.class) {
                return 1;
            }
            if (c22 == Byte.class) {
                return -4;
            }
            if (c22 == Short.class) {
                return -3;
            }
            return c22 == BigDecimal.class ? 3 : 0;
        }
        return 0;
    }
}
