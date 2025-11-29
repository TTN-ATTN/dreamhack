package org.apache.logging.log4j.message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.util.StringBuilders;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/message/ParameterFormatter.class */
final class ParameterFormatter {
    static final String RECURSION_PREFIX = "[...";
    static final String RECURSION_SUFFIX = "...]";
    static final String ERROR_PREFIX = "[!!!";
    static final String ERROR_SEPARATOR = "=>";
    static final String ERROR_MSG_SEPARATOR = ":";
    static final String ERROR_SUFFIX = "!!!]";
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final char ESCAPE_CHAR = '\\';
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_REF = ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    });

    private ParameterFormatter() {
    }

    static int countArgumentPlaceholders(final String messagePattern) {
        boolean z;
        if (messagePattern == null) {
            return 0;
        }
        int length = messagePattern.length();
        int result = 0;
        boolean isEscaped = false;
        int i = 0;
        while (i < length - 1) {
            char curChar = messagePattern.charAt(i);
            if (curChar == '\\') {
                z = !isEscaped;
            } else if (curChar == '{') {
                if (!isEscaped && messagePattern.charAt(i + 1) == '}') {
                    result++;
                    i++;
                }
                z = false;
            } else {
                z = false;
            }
            isEscaped = z;
            i++;
        }
        return result;
    }

    static int countArgumentPlaceholders2(final String messagePattern, final int[] indices) {
        if (messagePattern == null) {
            return 0;
        }
        int length = messagePattern.length();
        int result = 0;
        boolean isEscaped = false;
        int i = 0;
        while (i < length - 1) {
            char curChar = messagePattern.charAt(i);
            if (curChar == '\\') {
                isEscaped = !isEscaped;
                indices[0] = -1;
                result++;
            } else if (curChar == '{') {
                if (!isEscaped && messagePattern.charAt(i + 1) == '}') {
                    indices[result] = i;
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
            i++;
        }
        return result;
    }

    static int countArgumentPlaceholders3(final char[] messagePattern, final int length, final int[] indices) {
        boolean z;
        int result = 0;
        boolean isEscaped = false;
        int i = 0;
        while (i < length - 1) {
            char curChar = messagePattern[i];
            if (curChar == '\\') {
                z = !isEscaped;
            } else if (curChar == '{') {
                if (!isEscaped && messagePattern[i + 1] == '}') {
                    indices[result] = i;
                    result++;
                    i++;
                }
                z = false;
            } else {
                z = false;
            }
            isEscaped = z;
            i++;
        }
        return result;
    }

    static String format(final String messagePattern, final Object[] arguments) {
        StringBuilder result = new StringBuilder();
        int argCount = arguments == null ? 0 : arguments.length;
        formatMessage(result, messagePattern, arguments, argCount);
        return result.toString();
    }

    static void formatMessage2(final StringBuilder buffer, final String messagePattern, final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int previous = 0;
        for (int i = 0; i < argCount; i++) {
            buffer.append((CharSequence) messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer);
        }
        buffer.append((CharSequence) messagePattern, previous, messagePattern.length());
    }

    static void formatMessage3(final StringBuilder buffer, final char[] messagePattern, final int patternLength, final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null) {
            return;
        }
        if (arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int previous = 0;
        for (int i = 0; i < argCount; i++) {
            buffer.append(messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer);
        }
        buffer.append(messagePattern, previous, patternLength);
    }

    static void formatMessage(final StringBuilder buffer, final String messagePattern, final Object[] arguments, final int argCount) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int escapeCounter = 0;
        int currentArgument = 0;
        int i = 0;
        int len = messagePattern.length();
        while (i < len - 1) {
            char curChar = messagePattern.charAt(i);
            if (curChar == '\\') {
                escapeCounter++;
            } else {
                if (isDelimPair(curChar, messagePattern, i)) {
                    i++;
                    writeEscapedEscapeChars(escapeCounter, buffer);
                    if (isOdd(escapeCounter)) {
                        writeDelimPair(buffer);
                    } else {
                        writeArgOrDelimPair(arguments, argCount, currentArgument, buffer);
                        currentArgument++;
                    }
                } else {
                    handleLiteralChar(buffer, escapeCounter, curChar);
                }
                escapeCounter = 0;
            }
            i++;
        }
        handleRemainingCharIfAny(messagePattern, len, buffer, escapeCounter, i);
    }

    private static boolean isDelimPair(final char curChar, final String messagePattern, final int curCharIndex) {
        return curChar == '{' && messagePattern.charAt(curCharIndex + 1) == '}';
    }

    private static void handleRemainingCharIfAny(final String messagePattern, final int len, final StringBuilder buffer, final int escapeCounter, final int i) {
        if (i == len - 1) {
            char curChar = messagePattern.charAt(i);
            handleLastChar(buffer, escapeCounter, curChar);
        }
    }

    private static void handleLastChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        if (curChar == '\\') {
            writeUnescapedEscapeChars(escapeCounter + 1, buffer);
        } else {
            handleLiteralChar(buffer, escapeCounter, curChar);
        }
    }

    private static void handleLiteralChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        writeUnescapedEscapeChars(escapeCounter, buffer);
        buffer.append(curChar);
    }

    private static void writeDelimPair(final StringBuilder buffer) {
        buffer.append('{');
        buffer.append('}');
    }

    private static boolean isOdd(final int number) {
        return (number & 1) == 1;
    }

    private static void writeEscapedEscapeChars(final int escapeCounter, final StringBuilder buffer) {
        int escapedEscapes = escapeCounter >> 1;
        writeUnescapedEscapeChars(escapedEscapes, buffer);
    }

    private static void writeUnescapedEscapeChars(int escapeCounter, final StringBuilder buffer) {
        while (escapeCounter > 0) {
            buffer.append('\\');
            escapeCounter--;
        }
    }

    private static void writeArgOrDelimPair(final Object[] arguments, final int argCount, final int currentArgument, final StringBuilder buffer) {
        if (currentArgument < argCount) {
            recursiveDeepToString(arguments[currentArgument], buffer);
        } else {
            writeDelimPair(buffer);
        }
    }

    static String deepToString(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Integer) {
            return Integer.toString(((Integer) o).intValue());
        }
        if (o instanceof Long) {
            return Long.toString(((Long) o).longValue());
        }
        if (o instanceof Double) {
            return Double.toString(((Double) o).doubleValue());
        }
        if (o instanceof Boolean) {
            return Boolean.toString(((Boolean) o).booleanValue());
        }
        if (o instanceof Character) {
            return Character.toString(((Character) o).charValue());
        }
        if (o instanceof Short) {
            return Short.toString(((Short) o).shortValue());
        }
        if (o instanceof Float) {
            return Float.toString(((Float) o).floatValue());
        }
        if (o instanceof Byte) {
            return Byte.toString(((Byte) o).byteValue());
        }
        StringBuilder str = new StringBuilder();
        recursiveDeepToString(o, str);
        return str.toString();
    }

    static void recursiveDeepToString(final Object o, final StringBuilder str) {
        recursiveDeepToString(o, str, null);
    }

    private static void recursiveDeepToString(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        if (appendSpecialTypes(o, str)) {
            return;
        }
        if (isMaybeRecursive(o)) {
            appendPotentiallyRecursiveValue(o, str, dejaVu);
        } else {
            tryObjectToString(o, str);
        }
    }

    private static boolean appendSpecialTypes(final Object o, final StringBuilder str) {
        return StringBuilders.appendSpecificTypes(str, o) || appendDate(o, str);
    }

    private static boolean appendDate(final Object o, final StringBuilder str) {
        if (!(o instanceof Date)) {
            return false;
        }
        Date date = (Date) o;
        SimpleDateFormat format = SIMPLE_DATE_FORMAT_REF.get();
        str.append(format.format(date));
        return true;
    }

    private static boolean isMaybeRecursive(final Object o) {
        return o.getClass().isArray() || (o instanceof Map) || (o instanceof Collection);
    }

    private static void appendPotentiallyRecursiveValue(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Class<?> oClass = o.getClass();
        if (oClass.isArray()) {
            appendArray(o, str, dejaVu, oClass);
        } else if (o instanceof Map) {
            appendMap(o, str, dejaVu);
        } else {
            if (o instanceof Collection) {
                appendCollection(o, str, dejaVu);
                return;
            }
            throw new IllegalArgumentException("was expecting a container, found " + oClass);
        }
    }

    private static void appendArray(final Object o, final StringBuilder str, final Set<Object> dejaVu, final Class<?> oClass) {
        if (oClass == byte[].class) {
            str.append(Arrays.toString((byte[]) o));
            return;
        }
        if (oClass == short[].class) {
            str.append(Arrays.toString((short[]) o));
            return;
        }
        if (oClass == int[].class) {
            str.append(Arrays.toString((int[]) o));
            return;
        }
        if (oClass == long[].class) {
            str.append(Arrays.toString((long[]) o));
            return;
        }
        if (oClass == float[].class) {
            str.append(Arrays.toString((float[]) o));
            return;
        }
        if (oClass == double[].class) {
            str.append(Arrays.toString((double[]) o));
            return;
        }
        if (oClass == boolean[].class) {
            str.append(Arrays.toString((boolean[]) o));
            return;
        }
        if (oClass == char[].class) {
            str.append(Arrays.toString((char[]) o));
            return;
        }
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Object[] oArray = (Object[]) o;
        str.append('[');
        boolean first = true;
        for (Object current : oArray) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            recursiveDeepToString(current, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append(']');
    }

    private static void appendMap(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Map<?, ?> oMap = (Map) o;
        str.append('{');
        boolean isFirst = true;
        for (Object o1 : oMap.entrySet()) {
            Map.Entry<?, ?> current = (Map.Entry) o1;
            if (isFirst) {
                isFirst = false;
            } else {
                str.append(", ");
            }
            Object key = current.getKey();
            Object value = current.getValue();
            recursiveDeepToString(key, str, cloneDejaVu(effectiveDejaVu));
            str.append('=');
            recursiveDeepToString(value, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append('}');
    }

    private static void appendCollection(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Collection<?> oCol = (Collection) o;
        str.append('[');
        boolean isFirst = true;
        for (Object anOCol : oCol) {
            if (isFirst) {
                isFirst = false;
            } else {
                str.append(", ");
            }
            recursiveDeepToString(anOCol, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append(']');
    }

    private static Set<Object> getOrCreateDejaVu(Set<Object> dejaVu) {
        return dejaVu == null ? createDejaVu() : dejaVu;
    }

    private static Set<Object> createDejaVu() {
        return Collections.newSetFromMap(new IdentityHashMap());
    }

    private static Set<Object> cloneDejaVu(Set<Object> dejaVu) {
        Set<Object> clonedDejaVu = createDejaVu();
        clonedDejaVu.addAll(dejaVu);
        return clonedDejaVu;
    }

    private static void tryObjectToString(final Object o, final StringBuilder str) {
        try {
            str.append(o.toString());
        } catch (Throwable t) {
            handleErrorInObjectToString(o, str, t);
        }
    }

    private static void handleErrorInObjectToString(final Object o, final StringBuilder str, final Throwable t) {
        str.append("[!!!");
        str.append(identityToString(o));
        str.append("=>");
        String msg = t.getMessage();
        String className = t.getClass().getName();
        str.append(className);
        if (!className.equals(msg)) {
            str.append(":");
            str.append(msg);
        }
        str.append("!!!]");
    }

    static String identityToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }
}
