package freemarker.ext.jakarta.jsp;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibMethodUtil.class */
final class TaglibMethodUtil {
    private static final Pattern FUNCTION_SIGNATURE_PATTERN = Pattern.compile("^([\\w\\.]+(\\s*\\[\\s*\\])?)\\s+(\\w+)\\s*\\((.*)\\)$", 32);
    private static final Pattern FUNCTION_PARAMETER_PATTERN = Pattern.compile("^([\\w\\.]+)(\\s*\\[\\s*\\])?$");

    private TaglibMethodUtil() {
    }

    static Method getMethodByFunctionSignature(Class clazz, String signature) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
        Class[] paramTypes;
        Matcher m1 = FUNCTION_SIGNATURE_PATTERN.matcher(signature);
        if (!m1.matches()) {
            throw new IllegalArgumentException("Invalid function signature (doesn't match this pattern: " + FUNCTION_SIGNATURE_PATTERN + ")");
        }
        String methodName = m1.group(3);
        String params = m1.group(4).trim();
        if ("".equals(params)) {
            paramTypes = new Class[0];
        } else {
            String[] paramsArray = StringUtil.split(params, ',');
            paramTypes = new Class[paramsArray.length];
            for (int i = 0; i < paramsArray.length; i++) {
                String token = paramsArray[i].trim();
                Matcher m2 = FUNCTION_PARAMETER_PATTERN.matcher(token);
                if (!m2.matches()) {
                    throw new IllegalArgumentException("Invalid argument signature (doesn't match pattern " + FUNCTION_PARAMETER_PATTERN + "): " + token);
                }
                String paramType = m2.group(1);
                boolean isPrimitive = paramType.indexOf(46) == -1;
                boolean isArrayType = m2.group(2) != null;
                if (isPrimitive) {
                    if ("byte".equals(paramType)) {
                        paramTypes[i] = isArrayType ? byte[].class : Byte.TYPE;
                    } else if ("short".equals(paramType)) {
                        paramTypes[i] = isArrayType ? short[].class : Short.TYPE;
                    } else if ("int".equals(paramType)) {
                        paramTypes[i] = isArrayType ? int[].class : Integer.TYPE;
                    } else if ("long".equals(paramType)) {
                        paramTypes[i] = isArrayType ? long[].class : Long.TYPE;
                    } else if ("float".equals(paramType)) {
                        paramTypes[i] = isArrayType ? float[].class : Float.TYPE;
                    } else if ("double".equals(paramType)) {
                        paramTypes[i] = isArrayType ? double[].class : Double.TYPE;
                    } else if ("boolean".equals(paramType)) {
                        paramTypes[i] = isArrayType ? boolean[].class : Boolean.TYPE;
                    } else if ("char".equals(paramType)) {
                        paramTypes[i] = isArrayType ? char[].class : Character.TYPE;
                    } else {
                        throw new IllegalArgumentException("Invalid primitive type: '" + paramType + "'.");
                    }
                } else if (isArrayType) {
                    paramTypes[i] = ClassUtil.forName("[L" + paramType + ";");
                } else {
                    paramTypes[i] = ClassUtil.forName(paramType);
                }
            }
        }
        return clazz.getMethod(methodName, paramTypes);
    }
}
