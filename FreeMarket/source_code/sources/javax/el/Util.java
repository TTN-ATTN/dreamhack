package javax.el;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.cglib.core.Constants;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util.class */
class Util {
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final boolean IS_SECURITY_ENABLED;
    private static final boolean GET_CLASSLOADER_USE_PRIVILEGED;
    private static final CacheValue nullTcclFactory;
    private static final Map<CacheKey, CacheValue> factoryCache;

    Util() {
    }

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
        if (IS_SECURITY_ENABLED) {
            String value = (String) AccessController.doPrivileged(() -> {
                return System.getProperty("org.apache.el.GET_CLASSLOADER_USE_PRIVILEGED", "true");
            });
            GET_CLASSLOADER_USE_PRIVILEGED = Boolean.parseBoolean(value);
        } else {
            GET_CLASSLOADER_USE_PRIVILEGED = false;
        }
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap();
    }

    static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (t instanceof VirtualMachineError) {
            throw ((VirtualMachineError) t);
        }
    }

    static String message(ELContext context, String name, Object... props) {
        Locale locale = null;
        if (context != null) {
            locale = context.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
            if (locale == null) {
                return "";
            }
        }
        ResourceBundle bundle = ResourceBundle.getBundle("javax.el.LocalStrings", locale);
        try {
            String template = bundle.getString(name);
            if (props != null) {
                template = MessageFormat.format(template, props);
            }
            return template;
        } catch (MissingResourceException e) {
            return "Missing Resource: '" + name + "' for Locale " + locale.getDisplayName();
        }
    }

    static ExpressionFactory getExpressionFactory() {
        CacheValue cacheValue;
        ClassLoader tccl = getContextClassLoader();
        if (tccl == null) {
            cacheValue = nullTcclFactory;
        } else {
            CacheKey key = new CacheKey(tccl);
            cacheValue = factoryCache.get(key);
            if (cacheValue == null) {
                CacheValue newCacheValue = new CacheValue();
                cacheValue = factoryCache.putIfAbsent(key, newCacheValue);
                if (cacheValue == null) {
                    cacheValue = newCacheValue;
                }
            }
        }
        Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            ExpressionFactory factory = cacheValue.getExpressionFactory();
            readLock.unlock();
            if (factory == null) {
                Lock writeLock = cacheValue.getLock().writeLock();
                writeLock.lock();
                try {
                    factory = cacheValue.getExpressionFactory();
                    if (factory == null) {
                        factory = ExpressionFactory.newInstance();
                        cacheValue.setExpressionFactory(factory);
                    }
                } finally {
                    writeLock.unlock();
                }
            }
            return factory;
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$CacheKey.class */
    private static class CacheKey {
        private final int hash;
        private final WeakReference<ClassLoader> ref;

        CacheKey(ClassLoader key) {
            this.hash = key.hashCode();
            this.ref = new WeakReference<>(key);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            ClassLoader thisKey;
            if (obj == this) {
                return true;
            }
            return (obj instanceof CacheKey) && (thisKey = this.ref.get()) != null && thisKey == ((CacheKey) obj).ref.get();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$CacheValue.class */
    private static class CacheValue {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private WeakReference<ExpressionFactory> ref;

        CacheValue() {
        }

        public ReadWriteLock getLock() {
            return this.lock;
        }

        public ExpressionFactory getExpressionFactory() {
            if (this.ref != null) {
                return this.ref.get();
            }
            return null;
        }

        public void setExpressionFactory(ExpressionFactory factory) {
            this.ref = new WeakReference<>(factory);
        }
    }

    static Method findMethod(ELContext context, Class<?> clazz, Object base, String methodName, Class<?>[] paramTypes, Object[] paramValues) throws SecurityException {
        if (clazz == null || methodName == null) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", clazz, methodName, paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = getTypesFromValues(paramValues);
        }
        Method[] methods = clazz.getMethods();
        List<Wrapper<Method>> wrappers = Wrapper.wrap(methods, methodName);
        Wrapper<Method> result = findWrapper(context, clazz, wrappers, methodName, paramTypes, paramValues);
        return getMethod(clazz, base, result.unWrap());
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x010a, code lost:
    
        r26 = Integer.MAX_VALUE;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static <T> javax.el.Util.Wrapper<T> findWrapper(javax.el.ELContext r11, java.lang.Class<?> r12, java.util.List<javax.el.Util.Wrapper<T>> r13, java.lang.String r14, java.lang.Class<?>[] r15, java.lang.Object[] r16) {
        /*
            Method dump skipped, instructions count: 767
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.el.Util.findWrapper(javax.el.ELContext, java.lang.Class, java.util.List, java.lang.String, java.lang.Class[], java.lang.Object[]):javax.el.Util$Wrapper");
    }

    private static String paramString(Class<?>[] types) {
        if (types != null) {
            StringBuilder sb = new StringBuilder();
            for (Class<?> type : types) {
                if (type == null) {
                    sb.append("null, ");
                } else {
                    sb.append(type.getName()).append(", ");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return null;
    }

    private static <T> Wrapper<T> resolveAmbiguousWrapper(Set<Wrapper<T>> candidates, Class<?>[] paramTypes) {
        Wrapper<T> w = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        int i = 0;
        while (true) {
            if (i >= paramTypes.length) {
                break;
            }
            if (w.getParameterTypes()[i] == paramTypes[i]) {
                i++;
            } else {
                nonMatchIndex = i;
                nonMatchClass = paramTypes[i];
                break;
            }
        }
        if (nonMatchClass == null) {
            return null;
        }
        Iterator<Wrapper<T>> it = candidates.iterator();
        while (it.hasNext()) {
            if (it.next().getParameterTypes()[nonMatchIndex] == paramTypes[nonMatchIndex]) {
                return null;
            }
        }
        Class<?> superclass = nonMatchClass.getSuperclass();
        while (true) {
            Class<?> superClass = superclass;
            if (superClass != null) {
                for (Wrapper<T> c : candidates) {
                    if (c.getParameterTypes()[nonMatchIndex].equals(superClass)) {
                        return c;
                    }
                }
                superclass = superClass.getSuperclass();
            } else {
                Wrapper<T> match = null;
                if (Number.class.isAssignableFrom(nonMatchClass)) {
                    Iterator<Wrapper<T>> it2 = candidates.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        Wrapper<T> c2 = it2.next();
                        Class<?> candidateType = c2.getParameterTypes()[nonMatchIndex];
                        if (Number.class.isAssignableFrom(candidateType) || candidateType.isPrimitive()) {
                            if (match == null) {
                                match = c2;
                            } else {
                                match = null;
                                break;
                            }
                        }
                    }
                }
                return match;
            }
        }
    }

    static boolean isAssignableFrom(Class<?> src, Class<?> target) {
        Class<?> targetClass;
        if (src == null) {
            return true;
        }
        if (target.isPrimitive()) {
            if (target == Boolean.TYPE) {
                targetClass = Boolean.class;
            } else if (target == Character.TYPE) {
                targetClass = Character.class;
            } else if (target == Byte.TYPE) {
                targetClass = Byte.class;
            } else if (target == Short.TYPE) {
                targetClass = Short.class;
            } else if (target == Integer.TYPE) {
                targetClass = Integer.class;
            } else if (target == Long.TYPE) {
                targetClass = Long.class;
            } else if (target == Float.TYPE) {
                targetClass = Float.class;
            } else {
                targetClass = Double.class;
            }
        } else {
            targetClass = target;
        }
        return targetClass.isAssignableFrom(src);
    }

    private static boolean isCoercibleFrom(ELContext context, Object src, Class<?> target) {
        try {
            context.convertToType(src, target);
            return true;
        } catch (ELException e) {
            return false;
        }
    }

    private static Class<?>[] getTypesFromValues(Object[] values) {
        if (values == null) {
            return EMPTY_CLASS_ARRAY;
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                result[i] = null;
            } else {
                result[i] = values[i].getClass();
            }
        }
        return result;
    }

    static Method getMethod(Class<?> type, Object base, Method m) throws NoSuchMethodException, SecurityException {
        Method mp;
        JreCompat jreCompat = JreCompat.getInstance();
        if (m == null || (Modifier.isPublic(type.getModifiers()) && ((Modifier.isStatic(m.getModifiers()) && jreCompat.canAccess(null, m)) || jreCompat.canAccess(base, m)))) {
            return m;
        }
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                Method mp2 = iface.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp2.getDeclaringClass(), base, mp2);
            } catch (NoSuchMethodException e) {
            }
            if (mp != null) {
                return mp;
            }
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                Method mp3 = sup.getMethod(m.getName(), m.getParameterTypes());
                Method mp4 = getMethod(mp3.getDeclaringClass(), base, mp3);
                if (mp4 != null) {
                    return mp4;
                }
                return null;
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
        return null;
    }

    static Constructor<?> findConstructor(ELContext context, Class<?> clazz, Class<?>[] paramTypes, Object[] paramValues) throws SecurityException {
        if (clazz == null) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", null, Constants.CONSTRUCTOR_NAME, paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = getTypesFromValues(paramValues);
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        List<Wrapper<Constructor<?>>> wrappers = Wrapper.wrap(constructors);
        Wrapper<Constructor<?>> wrapper = findWrapper(context, clazz, wrappers, Constants.CONSTRUCTOR_NAME, paramTypes, paramValues);
        Constructor<?> constructor = wrapper.unWrap();
        JreCompat jreCompat = JreCompat.getInstance();
        if (!Modifier.isPublic(clazz.getModifiers()) || !jreCompat.canAccess(null, constructor)) {
            throw new MethodNotFoundException(message(null, "util.method.notfound", clazz, Constants.CONSTRUCTOR_NAME, paramString(paramTypes)));
        }
        return constructor;
    }

    static Object[] buildParameters(ELContext context, Class<?>[] parameterTypes, boolean isVarArgs, Object[] params) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NegativeArraySizeException {
        Object[] parameters = null;
        if (parameterTypes.length > 0) {
            parameters = new Object[parameterTypes.length];
            if (params == null) {
                params = EMPTY_OBJECT_ARRAY;
            }
            int paramCount = params.length;
            if (isVarArgs) {
                int varArgIndex = parameterTypes.length - 1;
                for (int i = 0; i < varArgIndex; i++) {
                    parameters[i] = context.convertToType(params[i], parameterTypes[i]);
                }
                Class<?> varArgClass = parameterTypes[varArgIndex].getComponentType();
                Object varargs = Array.newInstance(varArgClass, paramCount - varArgIndex);
                for (int i2 = varArgIndex; i2 < paramCount; i2++) {
                    Array.set(varargs, i2 - varArgIndex, context.convertToType(params[i2], varArgClass));
                }
                parameters[varArgIndex] = varargs;
            } else {
                parameters = new Object[parameterTypes.length];
                for (int i3 = 0; i3 < parameterTypes.length; i3++) {
                    parameters[i3] = context.convertToType(params[i3], parameterTypes[i3]);
                }
            }
        }
        return parameters;
    }

    static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (IS_SECURITY_ENABLED && GET_CLASSLOADER_USE_PRIVILEGED) {
            PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl();
            tccl = (ClassLoader) AccessController.doPrivileged(pa);
        } else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$Wrapper.class */
    private static abstract class Wrapper<T> {
        public abstract T unWrap();

        public abstract Class<?>[] getParameterTypes();

        public abstract boolean isVarArgs();

        public abstract boolean isBridge();

        private Wrapper() {
        }

        public static List<Wrapper<Method>> wrap(Method[] methods, String name) {
            List<Wrapper<Method>> result = new ArrayList<>();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    result.add(new MethodWrapper(method));
                }
            }
            return result;
        }

        public static List<Wrapper<Constructor<?>>> wrap(Constructor<?>[] constructors) {
            List<Wrapper<Constructor<?>>> result = new ArrayList<>();
            for (Constructor<?> constructor : constructors) {
                result.add(new ConstructorWrapper(constructor));
            }
            return result;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$MethodWrapper.class */
    private static class MethodWrapper extends Wrapper<Method> {
        private final Method m;

        MethodWrapper(Method m) {
            super();
            this.m = m;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // javax.el.Util.Wrapper
        public Method unWrap() {
            return this.m;
        }

        @Override // javax.el.Util.Wrapper
        public Class<?>[] getParameterTypes() {
            return this.m.getParameterTypes();
        }

        @Override // javax.el.Util.Wrapper
        public boolean isVarArgs() {
            return this.m.isVarArgs();
        }

        @Override // javax.el.Util.Wrapper
        public boolean isBridge() {
            return this.m.isBridge();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$ConstructorWrapper.class */
    private static class ConstructorWrapper extends Wrapper<Constructor<?>> {
        private final Constructor<?> c;

        ConstructorWrapper(Constructor<?> c) {
            super();
            this.c = c;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // javax.el.Util.Wrapper
        public Constructor<?> unWrap() {
            return this.c;
        }

        @Override // javax.el.Util.Wrapper
        public Class<?>[] getParameterTypes() {
            return this.c.getParameterTypes();
        }

        @Override // javax.el.Util.Wrapper
        public boolean isVarArgs() {
            return this.c.isVarArgs();
        }

        @Override // javax.el.Util.Wrapper
        public boolean isBridge() {
            return false;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$MatchResult.class */
    private static class MatchResult implements Comparable<MatchResult> {
        private final boolean varArgs;
        private final int exactCount;
        private final int assignableCount;
        private final int coercibleCount;
        private final int varArgsCount;
        private final boolean bridge;

        MatchResult(boolean varArgs, int exactCount, int assignableCount, int coercibleCount, int varArgsCount, boolean bridge) {
            this.varArgs = varArgs;
            this.exactCount = exactCount;
            this.assignableCount = assignableCount;
            this.coercibleCount = coercibleCount;
            this.varArgsCount = varArgsCount;
            this.bridge = bridge;
        }

        public boolean isVarArgs() {
            return this.varArgs;
        }

        public int getExactCount() {
            return this.exactCount;
        }

        public int getAssignableCount() {
            return this.assignableCount;
        }

        public int getCoercibleCount() {
            return this.coercibleCount;
        }

        public int getVarArgsCount() {
            return this.varArgsCount;
        }

        public boolean isBridge() {
            return this.bridge;
        }

        @Override // java.lang.Comparable
        public int compareTo(MatchResult o) {
            int cmp = Boolean.compare(o.isVarArgs(), isVarArgs());
            if (cmp == 0) {
                cmp = Integer.compare(getExactCount(), o.getExactCount());
                if (cmp == 0) {
                    cmp = Integer.compare(getAssignableCount(), o.getAssignableCount());
                    if (cmp == 0) {
                        cmp = Integer.compare(getCoercibleCount(), o.getCoercibleCount());
                        if (cmp == 0) {
                            cmp = Integer.compare(o.getVarArgsCount(), getVarArgsCount());
                            if (cmp == 0) {
                                cmp = Boolean.compare(o.isBridge(), isBridge());
                            }
                        }
                    }
                }
            }
            return cmp;
        }

        public boolean equals(Object o) {
            return o == this || (null != o && getClass().equals(o.getClass()) && ((MatchResult) o).getExactCount() == getExactCount() && ((MatchResult) o).getAssignableCount() == getAssignableCount() && ((MatchResult) o).getCoercibleCount() == getCoercibleCount() && ((MatchResult) o).getVarArgsCount() == getVarArgsCount() && ((MatchResult) o).isVarArgs() == isVarArgs() && ((MatchResult) o).isBridge() == isBridge());
        }

        public int hashCode() {
            int result = (31 * 1) + this.assignableCount;
            return (31 * ((31 * ((31 * ((31 * ((31 * result) + (this.bridge ? 1231 : 1237))) + this.coercibleCount)) + this.exactCount)) + (this.varArgs ? 1231 : 1237))) + this.varArgsCount;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/Util$PrivilegedGetTccl.class */
    private static class PrivilegedGetTccl implements PrivilegedAction<ClassLoader> {
        private PrivilegedGetTccl() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
