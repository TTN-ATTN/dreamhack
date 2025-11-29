package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/AllowAllMemberAccessPolicy.class */
final class AllowAllMemberAccessPolicy implements MemberAccessPolicy {
    public static final AllowAllMemberAccessPolicy INSTANCE = new AllowAllMemberAccessPolicy();
    public static final ClassMemberAccessPolicy CLASS_POLICY_INSTANCE = new ClassMemberAccessPolicy() { // from class: freemarker.ext.beans.AllowAllMemberAccessPolicy.1
        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isMethodExposed(Method method) {
            return true;
        }

        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isConstructorExposed(Constructor<?> constructor) {
            return true;
        }

        @Override // freemarker.ext.beans.ClassMemberAccessPolicy
        public boolean isFieldExposed(Field field) {
            return true;
        }
    };

    private AllowAllMemberAccessPolicy() {
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
        return CLASS_POLICY_INSTANCE;
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public boolean isToStringAlwaysExposed() {
        return true;
    }
}
