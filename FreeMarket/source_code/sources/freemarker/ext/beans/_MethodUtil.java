package freemarker.ext.beans;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import freemarker.core.BugException;
import freemarker.core._DelayedConversionToString;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/_MethodUtil.class */
public final class _MethodUtil {
    private _MethodUtil() {
    }

    public static int isMoreOrSameSpecificParameterType(Class specific, Class generic, boolean bugfixed, int ifHigherThan) {
        if (ifHigherThan >= 4) {
            return 0;
        }
        if (generic.isAssignableFrom(specific)) {
            return generic == specific ? 1 : 4;
        }
        boolean specificIsPrim = specific.isPrimitive();
        boolean genericIsPrim = generic.isPrimitive();
        if (!specificIsPrim) {
            return (ifHigherThan < 3 && bugfixed && !genericIsPrim && Number.class.isAssignableFrom(specific) && Number.class.isAssignableFrom(generic) && isWideningBoxedNumberConversion(specific, generic)) ? 3 : 0;
        }
        if (genericIsPrim) {
            return (ifHigherThan < 3 && isWideningPrimitiveNumberConversion(specific, generic)) ? 3 : 0;
        }
        if (bugfixed) {
            Class specificAsBoxed = ClassUtil.primitiveClassToBoxingClass(specific);
            if (specificAsBoxed == generic) {
                return 2;
            }
            if (generic.isAssignableFrom(specificAsBoxed)) {
                return 4;
            }
            return (ifHigherThan < 3 && Number.class.isAssignableFrom(specificAsBoxed) && Number.class.isAssignableFrom(generic) && isWideningBoxedNumberConversion(specificAsBoxed, generic)) ? 3 : 0;
        }
        return 0;
    }

    private static boolean isWideningPrimitiveNumberConversion(Class source, Class target) {
        if (target == Short.TYPE && source == Byte.TYPE) {
            return true;
        }
        if (target == Integer.TYPE && (source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        if (target == Long.TYPE && (source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        if (target == Float.TYPE && (source == Long.TYPE || source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        if (target != Double.TYPE) {
            return false;
        }
        if (source == Float.TYPE || source == Long.TYPE || source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE) {
            return true;
        }
        return false;
    }

    private static boolean isWideningBoxedNumberConversion(Class source, Class target) {
        if (target == Short.class && source == Byte.class) {
            return true;
        }
        if (target == Integer.class && (source == Short.class || source == Byte.class)) {
            return true;
        }
        if (target == Long.class && (source == Integer.class || source == Short.class || source == Byte.class)) {
            return true;
        }
        if (target == Float.class && (source == Long.class || source == Integer.class || source == Short.class || source == Byte.class)) {
            return true;
        }
        if (target != Double.class) {
            return false;
        }
        if (source == Float.class || source == Long.class || source == Integer.class || source == Short.class || source == Byte.class) {
            return true;
        }
        return false;
    }

    public static Set getAssignables(Class c1, Class c2) {
        Set s = new HashSet();
        collectAssignables(c1, c2, s);
        return s;
    }

    private static void collectAssignables(Class c1, Class c2, Set s) {
        if (c1.isAssignableFrom(c2)) {
            s.add(c1);
        }
        Class sc = c1.getSuperclass();
        if (sc != null) {
            collectAssignables(sc, c2, s);
        }
        Class[] itf = c1.getInterfaces();
        for (Class cls : itf) {
            collectAssignables(cls, c2, s);
        }
    }

    public static Class[] getParameterTypes(Member member) {
        if (member instanceof Method) {
            return ((Method) member).getParameterTypes();
        }
        if (member instanceof Constructor) {
            return ((Constructor) member).getParameterTypes();
        }
        throw new IllegalArgumentException("\"member\" must be Method or Constructor");
    }

    public static boolean isVarargs(Member member) {
        if (member instanceof Method) {
            return ((Method) member).isVarArgs();
        }
        if (member instanceof Constructor) {
            return ((Constructor) member).isVarArgs();
        }
        throw new BugException();
    }

    public static String toString(Member member) {
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            throw new IllegalArgumentException("\"member\" must be a Method or Constructor");
        }
        StringBuilder sb = new StringBuilder();
        if ((member.getModifiers() & 8) != 0) {
            sb.append("static ");
        }
        String className = ClassUtil.getShortClassName(member.getDeclaringClass());
        if (className != null) {
            sb.append(className);
            sb.append('.');
        }
        sb.append(member.getName());
        sb.append('(');
        Class[] paramTypes = getParameterTypes(member);
        for (int i = 0; i < paramTypes.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            String paramTypeDecl = ClassUtil.getShortClassName(paramTypes[i]);
            if (i == paramTypes.length - 1 && paramTypeDecl.endsWith(ClassUtils.ARRAY_SUFFIX) && isVarargs(member)) {
                sb.append(paramTypeDecl.substring(0, paramTypeDecl.length() - 2));
                sb.append("...");
            } else {
                sb.append(paramTypeDecl);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public static Object[] invocationErrorMessageStart(Member member) {
        return invocationErrorMessageStart(member, member instanceof Constructor);
    }

    private static Object[] invocationErrorMessageStart(Object member, boolean isConstructor) {
        Object[] objArr = new Object[3];
        objArr[0] = "Java ";
        objArr[1] = isConstructor ? "constructor " : "method ";
        objArr[2] = new _DelayedJQuote(member);
        return objArr;
    }

    public static TemplateModelException newInvocationTemplateModelException(Object object, Member member, Throwable e) {
        return newInvocationTemplateModelException(object, member, (member.getModifiers() & 8) != 0, member instanceof Constructor, e);
    }

    public static TemplateModelException newInvocationTemplateModelException(Object object, CallableMemberDescriptor callableMemberDescriptor, Throwable e) {
        return newInvocationTemplateModelException(object, new _DelayedConversionToString(callableMemberDescriptor) { // from class: freemarker.ext.beans._MethodUtil.1
            @Override // freemarker.core._DelayedConversionToString
            protected String doConversion(Object callableMemberDescriptor2) {
                return ((CallableMemberDescriptor) callableMemberDescriptor2).getDeclaration();
            }
        }, callableMemberDescriptor.isStatic(), callableMemberDescriptor.isConstructor(), e);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static TemplateModelException newInvocationTemplateModelException(Object obj, Object obj2, boolean z, boolean z2, Throwable th) {
        Throwable targetException;
        while ((th instanceof InvocationTargetException) && (targetException = ((InvocationTargetException) th).getTargetException()) != null) {
            th = targetException;
        }
        Throwable th2 = th;
        Object[] objArr = new Object[4];
        objArr[0] = invocationErrorMessageStart(obj2, z2);
        objArr[1] = " threw an exception";
        objArr[2] = (z || z2) ? "" : new Object[]{" when invoked on ", obj.getClass(), " object ", new _DelayedJQuote(obj)};
        objArr[3] = "; see cause exception in the Java stack trace.";
        return new _TemplateModelException(th2, objArr);
    }

    public static String getBeanPropertyNameFromReaderMethodName(String name, Class<?> returnType) {
        int start;
        if (name.startsWith(BeanUtil.PREFIX_GETTER_GET)) {
            start = 3;
        } else if (returnType == Boolean.TYPE && name.startsWith(BeanUtil.PREFIX_GETTER_IS)) {
            start = 2;
        } else {
            return null;
        }
        int ln = name.length();
        if (start == ln) {
            return null;
        }
        char c1 = name.charAt(start);
        if (start + 1 < ln && Character.isUpperCase(name.charAt(start + 1)) && Character.isUpperCase(c1)) {
            return name.substring(start);
        }
        return new StringBuilder(ln - start).append(Character.toLowerCase(c1)).append((CharSequence) name, start + 1, ln).toString();
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> cls, Method method, Class<T> cls2) {
        T t = (T) method.getAnnotation(cls2);
        if (t != null) {
            return t;
        }
        return (T) getInheritableMethodAnnotation(cls, method.getName(), method.getParameterTypes(), true, cls2);
    }

    private static <T extends Annotation> T getInheritableMethodAnnotation(Class<?> cls, String str, Class<?>[] clsArr, boolean z, Class<T> cls2) throws NoSuchMethodException, SecurityException {
        Method method;
        T t;
        Method method2;
        T t2;
        if (!z) {
            try {
                method = cls.getMethod(str, clsArr);
            } catch (NoSuchMethodException e) {
                method = null;
            }
            if (method != null && (t = (T) method.getAnnotation(cls2)) != null) {
                return t;
            }
        }
        for (Class<?> cls3 : cls.getInterfaces()) {
            if (!cls3.getName().startsWith("java.")) {
                try {
                    method2 = cls3.getMethod(str, clsArr);
                } catch (NoSuchMethodException e2) {
                    method2 = null;
                }
                if (method2 != null && (t2 = (T) method2.getAnnotation(cls2)) != null) {
                    return t2;
                }
            }
        }
        Class<? super Object> superclass = cls.getSuperclass();
        if (superclass == Object.class || superclass == null) {
            return null;
        }
        return (T) getInheritableMethodAnnotation(superclass, str, clsArr, false, cls2);
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> cls, Constructor<?> constructor, Class<T> cls2) throws NoSuchMethodException, SecurityException {
        Constructor<?> constructor2;
        T t;
        T t2 = (T) constructor.getAnnotation(cls2);
        if (t2 != null) {
            return t2;
        }
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        while (true) {
            cls = cls.getSuperclass();
            if (cls == Object.class || cls == null) {
                return null;
            }
            try {
                constructor2 = cls.getConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                constructor2 = null;
            }
            if (constructor2 != null && (t = (T) constructor2.getAnnotation(cls2)) != null) {
                return t;
            }
        }
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> cls, Field field, Class<T> cls2) {
        T t = (T) field.getAnnotation(cls2);
        if (t != null) {
            return t;
        }
        return (T) getInheritableFieldAnnotation(cls, field.getName(), true, cls2);
    }

    private static <T extends Annotation> T getInheritableFieldAnnotation(Class<?> cls, String str, boolean z, Class<T> cls2) throws NoSuchFieldException {
        Field field;
        T t;
        Field field2;
        T t2;
        if (!z) {
            try {
                field = cls.getField(str);
            } catch (NoSuchFieldException e) {
                field = null;
            }
            if (field != null && (t = (T) field.getAnnotation(cls2)) != null) {
                return t;
            }
        }
        for (Class<?> cls3 : cls.getInterfaces()) {
            if (!cls3.getName().startsWith("java.")) {
                try {
                    field2 = cls3.getField(str);
                } catch (NoSuchFieldException e2) {
                    field2 = null;
                }
                if (field2 != null && (t2 = (T) field2.getAnnotation(cls2)) != null) {
                    return t2;
                }
            }
        }
        Class<? super Object> superclass = cls.getSuperclass();
        if (superclass == Object.class || superclass == null) {
            return null;
        }
        return (T) getInheritableFieldAnnotation(superclass, str, false, cls2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x0077, code lost:
    
        r0 = getMethodWithClosestNonSubInterfaceReturnType(r3, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x007e, code lost:
    
        if (r0 == null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0082, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0083, code lost:
    
        r0 = r4.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0092, code lost:
    
        if (r0.hasNext() == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0095, code lost:
    
        r0 = r0.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a8, code lost:
    
        if (r0.getReturnType() != java.lang.Object.class) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ad, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00b1, code lost:
    
        return null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.reflect.Method getMethodWithClosestNonSubReturnType(java.lang.Class<?> r3, java.util.Collection<java.lang.reflect.Method> r4) {
        /*
            r0 = r4
            java.util.Iterator r0 = r0.iterator()
            r5 = r0
        L7:
            r0 = r5
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L27
            r0 = r5
            java.lang.Object r0 = r0.next()
            java.lang.reflect.Method r0 = (java.lang.reflect.Method) r0
            r6 = r0
            r0 = r6
            java.lang.Class r0 = r0.getReturnType()
            r1 = r3
            if (r0 != r1) goto L24
            r0 = r6
            return r0
        L24:
            goto L7
        L27:
            r0 = r3
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            if (r0 == r1) goto L34
            r0 = r3
            boolean r0 = r0.isPrimitive()
            if (r0 == 0) goto L36
        L34:
            r0 = 0
            return r0
        L36:
            r0 = r3
            java.lang.Class r0 = r0.getSuperclass()
            r5 = r0
        L3b:
            r0 = r5
            if (r0 == 0) goto L77
            r0 = r5
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            if (r0 == r1) goto L77
            r0 = r4
            java.util.Iterator r0 = r0.iterator()
            r6 = r0
        L4c:
            r0 = r6
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L6f
            r0 = r6
            java.lang.Object r0 = r0.next()
            java.lang.reflect.Method r0 = (java.lang.reflect.Method) r0
            r7 = r0
            r0 = r7
            java.lang.Class r0 = r0.getReturnType()
            r1 = r5
            if (r0 != r1) goto L6c
            r0 = r7
            return r0
        L6c:
            goto L4c
        L6f:
            r0 = r5
            java.lang.Class r0 = r0.getSuperclass()
            r5 = r0
            goto L3b
        L77:
            r0 = r3
            r1 = r4
            java.lang.reflect.Method r0 = getMethodWithClosestNonSubInterfaceReturnType(r0, r1)
            r6 = r0
            r0 = r6
            if (r0 == 0) goto L83
            r0 = r6
            return r0
        L83:
            r0 = r4
            java.util.Iterator r0 = r0.iterator()
            r7 = r0
        L8b:
            r0 = r7
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto Lb1
            r0 = r7
            java.lang.Object r0 = r0.next()
            java.lang.reflect.Method r0 = (java.lang.reflect.Method) r0
            r8 = r0
            r0 = r8
            java.lang.Class r0 = r0.getReturnType()
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            if (r0 != r1) goto Lae
            r0 = r8
            return r0
        Lae:
            goto L8b
        Lb1:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.ext.beans._MethodUtil.getMethodWithClosestNonSubReturnType(java.lang.Class, java.util.Collection):java.lang.reflect.Method");
    }

    private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods) {
        HashSet<Class<?>> nullResultReturnTypeInterfaces = new HashSet<>();
        do {
            Method result = getMethodWithClosestNonSubInterfaceReturnType(returnType, methods, nullResultReturnTypeInterfaces);
            if (result != null) {
                return result;
            }
            returnType = returnType.getSuperclass();
        } while (returnType != null);
        return null;
    }

    private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods, Set<Class<?>> nullResultReturnTypeInterfaces) {
        boolean returnTypeIsInterface = returnType.isInterface();
        if (returnTypeIsInterface) {
            if (nullResultReturnTypeInterfaces.contains(returnType)) {
                return null;
            }
            for (Method method : methods) {
                if (method.getReturnType() == returnType) {
                    return method;
                }
            }
        }
        for (Class<?> subInterface : returnType.getInterfaces()) {
            Method result = getMethodWithClosestNonSubInterfaceReturnType(subInterface, methods, nullResultReturnTypeInterfaces);
            if (result != null) {
                return result;
            }
        }
        if (returnTypeIsInterface) {
            nullResultReturnTypeInterfaces.add(returnType);
            return null;
        }
        return null;
    }
}
