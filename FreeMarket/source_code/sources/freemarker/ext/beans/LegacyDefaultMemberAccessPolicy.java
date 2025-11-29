package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/LegacyDefaultMemberAccessPolicy.class */
public final class LegacyDefaultMemberAccessPolicy implements MemberAccessPolicy {
    private static final String UNSAFE_METHODS_PROPERTIES = "unsafeMethods.properties";
    public static final LegacyDefaultMemberAccessPolicy INSTANCE = new LegacyDefaultMemberAccessPolicy();
    private static final Set<Method> UNSAFE_METHODS = createUnsafeMethodsSet();
    private static final BlacklistClassMemberAccessPolicy CLASS_MEMBER_ACCESS_POLICY_INSTANCE = new BlacklistClassMemberAccessPolicy();

    private static Set<Method> createUnsafeMethodsSet() throws ReflectiveOperationException {
        try {
            Properties props = ClassUtil.loadProperties(BeansWrapper.class, UNSAFE_METHODS_PROPERTIES);
            Set<Method> set = new HashSet<>((props.size() * 4) / 3, 1.0f);
            for (Object key : props.keySet()) {
                try {
                    set.add(parseMethodSpec((String) key));
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    if (ClassIntrospector.DEVELOPMENT_MODE) {
                        throw e;
                    }
                }
            }
            return set;
        } catch (Exception e2) {
            throw new RuntimeException("Could not load unsafe method set", e2);
        }
    }

    private static Method parseMethodSpec(String methodSpec) throws NoSuchMethodException, ClassNotFoundException {
        int brace = methodSpec.indexOf(40);
        int dot = methodSpec.lastIndexOf(46, brace);
        Class<?> clazz = ClassUtil.forName(methodSpec.substring(0, dot));
        String methodName = methodSpec.substring(dot + 1, brace);
        String argSpec = methodSpec.substring(brace + 1, methodSpec.length() - 1);
        StringTokenizer tok = new StringTokenizer(argSpec, ",");
        int argcount = tok.countTokens();
        Class<?>[] argTypes = new Class[argcount];
        for (int i = 0; i < argcount; i++) {
            String argClassName = tok.nextToken();
            argTypes[i] = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
            if (argTypes[i] == null) {
                argTypes[i] = ClassUtil.forName(argClassName);
            }
        }
        return clazz.getMethod(methodName, argTypes);
    }

    private LegacyDefaultMemberAccessPolicy() {
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public ClassMemberAccessPolicy forClass(Class<?> containingClass) {
        return CLASS_MEMBER_ACCESS_POLICY_INSTANCE;
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public boolean isToStringAlwaysExposed() {
        return true;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/LegacyDefaultMemberAccessPolicy$BlacklistClassMemberAccessPolicy.class */
    private static class BlacklistClassMemberAccessPolicy implements ClassMemberAccessPolicy {
        private BlacklistClassMemberAccessPolicy() {
        }

        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isMethodExposed(Method method) {
            return !LegacyDefaultMemberAccessPolicy.UNSAFE_METHODS.contains(method);
        }

        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isConstructorExposed(Constructor<?> constructor) {
            return true;
        }

        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isFieldExposed(Field field) {
            return true;
        }
    }
}
