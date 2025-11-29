package freemarker.ext.beans;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberAccessPolicy.class */
public interface MemberAccessPolicy {
    ClassMemberAccessPolicy forClass(Class<?> cls);

    boolean isToStringAlwaysExposed();
}
