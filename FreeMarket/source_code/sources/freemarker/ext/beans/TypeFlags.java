package freemarker.ext.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/TypeFlags.class */
class TypeFlags {
    static final int WIDENED_NUMERICAL_UNWRAPPING_HINT = 1;
    static final int BYTE = 4;
    static final int SHORT = 8;
    static final int INTEGER = 16;
    static final int LONG = 32;
    static final int FLOAT = 64;
    static final int DOUBLE = 128;
    static final int BIG_INTEGER = 256;
    static final int BIG_DECIMAL = 512;
    static final int UNKNOWN_NUMERICAL_TYPE = 1024;
    static final int ACCEPTS_NUMBER = 2048;
    static final int ACCEPTS_DATE = 4096;
    static final int ACCEPTS_STRING = 8192;
    static final int ACCEPTS_BOOLEAN = 16384;
    static final int ACCEPTS_MAP = 32768;
    static final int ACCEPTS_LIST = 65536;
    static final int ACCEPTS_SET = 131072;
    static final int ACCEPTS_ARRAY = 262144;
    static final int CHARACTER = 524288;
    static final int ACCEPTS_ANY_OBJECT = 522240;
    static final int MASK_KNOWN_INTEGERS = 316;
    static final int MASK_KNOWN_NONINTEGERS = 704;
    static final int MASK_ALL_KNOWN_NUMERICALS = 1020;
    static final int MASK_ALL_NUMERICALS = 2044;

    TypeFlags() {
    }

    static int classToTypeFlags(Class pClass) {
        if (pClass == Object.class) {
            return ACCEPTS_ANY_OBJECT;
        }
        if (pClass == String.class) {
            return 8192;
        }
        if (pClass.isPrimitive()) {
            if (pClass == Integer.TYPE) {
                return 2064;
            }
            if (pClass == Long.TYPE) {
                return 2080;
            }
            if (pClass == Double.TYPE) {
                return 2176;
            }
            if (pClass == Float.TYPE) {
                return 2112;
            }
            if (pClass == Byte.TYPE) {
                return 2052;
            }
            if (pClass == Short.TYPE) {
                return 2056;
            }
            if (pClass == Character.TYPE) {
                return 524288;
            }
            return pClass == Boolean.TYPE ? 16384 : 0;
        }
        if (Number.class.isAssignableFrom(pClass)) {
            if (pClass == Integer.class) {
                return 2064;
            }
            if (pClass == Long.class) {
                return 2080;
            }
            if (pClass == Double.class) {
                return 2176;
            }
            if (pClass == Float.class) {
                return 2112;
            }
            if (pClass == Byte.class) {
                return 2052;
            }
            if (pClass == Short.class) {
                return 2056;
            }
            if (BigDecimal.class.isAssignableFrom(pClass)) {
                return 2560;
            }
            return BigInteger.class.isAssignableFrom(pClass) ? 2304 : 3072;
        }
        if (pClass.isArray()) {
            return 262144;
        }
        int flags = 0;
        if (pClass.isAssignableFrom(String.class)) {
            flags = 0 | 8192;
        }
        if (pClass.isAssignableFrom(Date.class)) {
            flags |= 4096;
        }
        if (pClass.isAssignableFrom(Boolean.class)) {
            flags |= 16384;
        }
        if (pClass.isAssignableFrom(Map.class)) {
            flags |= 32768;
        }
        if (pClass.isAssignableFrom(List.class)) {
            flags |= 65536;
        }
        if (pClass.isAssignableFrom(Set.class)) {
            flags |= 131072;
        }
        if (pClass == Character.class) {
            flags |= 524288;
        }
        return flags;
    }
}
