package freemarker.ext.beans;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberMatcher.class */
abstract class MemberMatcher<M extends Member, S> {
    private final Map<S, Types> signaturesToUpperBoundTypes = new HashMap();

    protected abstract S toMemberSignature(M m);

    protected abstract boolean matchInUpperBoundTypeSubtypes();

    MemberMatcher() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberMatcher$Types.class */
    private static class Types {
        private final Set<Class<?>> set;
        private boolean containsInterfaces;

        private Types() {
            this.set = new HashSet();
        }
    }

    void addMatching(Class<?> upperBoundType, M member) {
        Class<?> declaringClass = member.getDeclaringClass();
        if (!declaringClass.isAssignableFrom(upperBoundType)) {
            throw new IllegalArgumentException("Upper bound class " + upperBoundType.getName() + " is not the same type or a subtype of the declaring type of member " + member + ".");
        }
        S memberSignature = toMemberSignature(member);
        Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
        if (upperBoundTypes == null) {
            upperBoundTypes = new Types();
            this.signaturesToUpperBoundTypes.put(memberSignature, upperBoundTypes);
        }
        upperBoundTypes.set.add(upperBoundType);
        if (upperBoundType.isInterface()) {
            upperBoundTypes.containsInterfaces = true;
        }
    }

    boolean matches(Class<?> contextClass, M member) {
        S memberSignature = toMemberSignature(member);
        Types upperBoundTypes = this.signaturesToUpperBoundTypes.get(memberSignature);
        return upperBoundTypes != null && (!matchInUpperBoundTypeSubtypes() ? !containsExactType(upperBoundTypes, contextClass) : !containsTypeOrSuperType(upperBoundTypes, contextClass));
    }

    private static boolean containsExactType(Types types, Class<?> c) {
        if (c != null) {
            return types.set.contains(c);
        }
        return false;
    }

    private static boolean containsTypeOrSuperType(Types types, Class<?> c) {
        if (c != null) {
            if (!types.set.contains(c) && !containsTypeOrSuperType(types, c.getSuperclass())) {
                if (types.containsInterfaces) {
                    for (Class<?> anInterface : c.getInterfaces()) {
                        if (containsTypeOrSuperType(types, anInterface)) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return true;
        }
        return false;
    }
}
