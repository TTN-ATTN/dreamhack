package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/LocalVariableTableParameterNameDiscoverer.class */
public class LocalVariableTableParameterNameDiscoverer implements ParameterNameDiscoverer {
    private static final Log logger = LogFactory.getLog((Class<?>) LocalVariableTableParameterNameDiscoverer.class);
    private static final Map<Executable, String[]> NO_DEBUG_INFO_MAP = Collections.emptyMap();
    private final Map<Class<?>, Map<Executable, String[]>> parameterNamesCache = new ConcurrentHashMap(32);

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) throws IllegalArgumentException {
        Method originalMethod = BridgeMethodResolver.findBridgedMethod(method);
        return doGetParameterNames(originalMethod);
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        return doGetParameterNames(ctor);
    }

    @Nullable
    private String[] doGetParameterNames(Executable executable) {
        Class<?> declaringClass = executable.getDeclaringClass();
        Map<Executable, String[]> map = this.parameterNamesCache.computeIfAbsent(declaringClass, this::inspectClass);
        if (map != NO_DEBUG_INFO_MAP) {
            return map.get(executable);
        }
        return null;
    }

    private Map<Executable, String[]> inspectClass(Class<?> clazz) throws IOException {
        InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
        try {
            if (is == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot find '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names");
                }
                return NO_DEBUG_INFO_MAP;
            }
            try {
                ClassReader classReader = new ClassReader(is);
                Map<Executable, String[]> map = new ConcurrentHashMap<>(32);
                classReader.accept(new ParameterNameDiscoveringVisitor(clazz, map), 0);
                try {
                    is.close();
                } catch (IOException e) {
                }
                return map;
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Exception thrown while reading '.class' file for class [" + clazz + "] - unable to determine constructor/method parameter names", ex);
                }
                try {
                    is.close();
                } catch (IOException e2) {
                }
                return NO_DEBUG_INFO_MAP;
            } catch (IllegalArgumentException ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("ASM ClassReader failed to parse class file [" + clazz + "], probably due to a new Java class file version that isn't supported yet - unable to determine constructor/method parameter names", ex2);
                }
                try {
                    is.close();
                } catch (IOException e3) {
                }
                return NO_DEBUG_INFO_MAP;
            }
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e4) {
            }
            throw th;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/LocalVariableTableParameterNameDiscoverer$ParameterNameDiscoveringVisitor.class */
    private static class ParameterNameDiscoveringVisitor extends ClassVisitor {
        private static final String STATIC_CLASS_INIT = "<clinit>";
        private final Class<?> clazz;
        private final Map<Executable, String[]> executableMap;

        public ParameterNameDiscoveringVisitor(Class<?> clazz, Map<Executable, String[]> executableMap) {
            super(17432576);
            this.clazz = clazz;
            this.executableMap = executableMap;
        }

        @Override // org.springframework.asm.ClassVisitor
        @Nullable
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!isSyntheticOrBridged(access) && !"<clinit>".equals(name)) {
                return new LocalVariableTableVisitor(this.clazz, this.executableMap, name, desc, isStatic(access));
            }
            return null;
        }

        private static boolean isSyntheticOrBridged(int access) {
            return ((access & 4096) | (access & 64)) > 0;
        }

        private static boolean isStatic(int access) {
            return (access & 8) > 0;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/LocalVariableTableParameterNameDiscoverer$LocalVariableTableVisitor.class */
    private static class LocalVariableTableVisitor extends MethodVisitor {
        private static final String CONSTRUCTOR = "<init>";
        private final Class<?> clazz;
        private final Map<Executable, String[]> executableMap;
        private final String name;
        private final Type[] args;
        private final String[] parameterNames;
        private final boolean isStatic;
        private boolean hasLvtInfo;
        private final int[] lvtSlotIndex;

        public LocalVariableTableVisitor(Class<?> clazz, Map<Executable, String[]> map, String name, String desc, boolean isStatic) {
            super(17432576);
            this.hasLvtInfo = false;
            this.clazz = clazz;
            this.executableMap = map;
            this.name = name;
            this.args = Type.getArgumentTypes(desc);
            this.parameterNames = new String[this.args.length];
            this.isStatic = isStatic;
            this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
        }

        @Override // org.springframework.asm.MethodVisitor
        public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
            this.hasLvtInfo = true;
            for (int i = 0; i < this.lvtSlotIndex.length; i++) {
                if (this.lvtSlotIndex[i] == index) {
                    this.parameterNames[i] = name;
                }
            }
        }

        @Override // org.springframework.asm.MethodVisitor
        public void visitEnd() {
            if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
                this.executableMap.put(resolveExecutable(), this.parameterNames);
            }
        }

        private Executable resolveExecutable() {
            ClassLoader loader = this.clazz.getClassLoader();
            Class<?>[] argTypes = new Class[this.args.length];
            for (int i = 0; i < this.args.length; i++) {
                argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
            }
            try {
                if ("<init>".equals(this.name)) {
                    return this.clazz.getDeclaredConstructor(argTypes);
                }
                return this.clazz.getDeclaredMethod(this.name, argTypes);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Method [" + this.name + "] was discovered in the .class file but cannot be resolved in the class object", ex);
            }
        }

        private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
            int[] lvtIndex = new int[paramTypes.length];
            int nextIndex = isStatic ? 0 : 1;
            for (int i = 0; i < paramTypes.length; i++) {
                lvtIndex[i] = nextIndex;
                if (isWideType(paramTypes[i])) {
                    nextIndex += 2;
                } else {
                    nextIndex++;
                }
            }
            return lvtIndex;
        }

        private static boolean isWideType(Type aType) {
            return aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE;
        }
    }
}
